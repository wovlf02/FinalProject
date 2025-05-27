package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.rest.request.*;
import com.hamcam.back.dto.study.team.response.*;
import com.hamcam.back.dto.study.team.response.inner.TeamRoomSimpleInfo;
import com.hamcam.back.dto.study.team.rest.response.TeamRoomDetailResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.*;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.*;
import com.hamcam.back.util.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamStudyService {

    private final StudyRoomRepository studyRoomRepository;
    private final QuizRoomRepository quizRoomRepository;
    private final FocusRoomRepository focusRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    /** ✅ 팀방 생성 (Quiz or Focus) */
    public Long createRoom(TeamRoomCreateRequest request, Long userId) {
        User user = getUser(userId);
        StudyRoom room;

        if (request.getRoomType() == RoomType.QUIZ) {
            room = QuizRoom.builder()
                    .title(request.getTitle())
                    .password(request.getPassword())
                    .inviteCode(generateInviteCode())
                    .subject(request.getSubject())
                    .grade(request.getGrade())
                    .month(request.getMonth())
                    .difficulty(request.getDifficulty())
                    .problemId(request.getProblemId())
                    .isOngoing(false)
                    .isActive(true)
                    .build();
        } else {
            room = FocusRoom.builder()
                    .title(request.getTitle())
                    .password(request.getPassword())
                    .inviteCode(generateInviteCode())
                    .goalMinutes(request.getGoalMinutes())
                    .isFinished(false)
                    .winnerUserId(null)
                    .isActive(true)
                    .build();
        }

        studyRoomRepository.save(room);

        participantRepository.save(
                StudyRoomParticipant.builder()
                        .room(room)
                        .user(user)
                        .isHost(true)
                        .isReady(true)
                        .focusedMinutes(0)
                        .build()
        );

        return room.getId();
    }

    /** ✅ 입장 */
    public void enterRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);

        boolean exists = participantRepository.existsByRoomAndUser(room, user);
        if (!exists) {
            participantRepository.save(
                    StudyRoomParticipant.builder()
                            .room(room)
                            .user(user)
                            .isHost(false)
                            .isReady(true)
                            .focusedMinutes(0)
                            .build()
            );
        }
    }

    /** ✅ 나가기 */
    public void leaveRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);
        participantRepository.findByRoomAndUser(room, user)
                .ifPresent(participantRepository::delete);
    }

    /** ✅ 삭제 (방장만 가능) */
    public void deleteRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);

        StudyRoomParticipant participant = participantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("방 참가자가 아닙니다."));

        if (!participant.isHost()) {
            throw new IllegalArgumentException("방장만 삭제할 수 있습니다.");
        }

        participantRepository.deleteAllByRoom(room);
        if (room instanceof QuizRoom quizRoom) {
            quizRoomRepository.delete(quizRoom);
        } else if (room instanceof FocusRoom focusRoom) {
            focusRoomRepository.delete(focusRoom);
        } else {
            studyRoomRepository.delete(room);
        }

        redisService.deleteRoomLogs(roomId);
    }

    /** ✅ 방 상세 조회 */
    public TeamRoomDetailResponse getRoomDetail(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        getUser(userId); // 유효성 체크
        List<StudyRoomParticipant> participants = participantRepository.findAllByRoom(room);
        return TeamRoomDetailResponse.of(room, participants);
    }

    /** ✅ 나의 팀방 리스트 */
    public TeamRoomListResponse getMyRoomList(Long userId) {
        User user = getUser(userId);
        List<StudyRoomParticipant> list = participantRepository.findAllByUser(user);
        return TeamRoomListResponse.of(list);
    }
//
//    /** ✅ 전체 활성 팀방 리스트 */
    public TeamRoomListResponse getAllActiveRoomList() {
        List<StudyRoom> activeRooms = studyRoomRepository.findByIsActiveTrue();
        List<TeamRoomSimpleInfo> infoList = activeRooms.stream()
                .map(TeamRoomSimpleInfo::from)
                .toList();
        return TeamRoomListResponse.builder().rooms(infoList).build();
    }

    // ===== 내부 유틸 =====

    private String generateInviteCode() {
        return "C" + System.currentTimeMillis();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private StudyRoom getRoom(Long roomId) {
        return studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("팀방이 존재하지 않습니다."));
    }
}
