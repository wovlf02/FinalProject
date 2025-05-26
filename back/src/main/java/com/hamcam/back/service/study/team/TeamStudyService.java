package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.study.team.request.*;
import com.hamcam.back.dto.study.team.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.*;
import com.hamcam.back.entity.study.team.FocusRoom;
import com.hamcam.back.entity.study.team.QuizRoom;
import com.hamcam.back.entity.study.team.RoomType;
import com.hamcam.back.entity.study.team.StudyRoom;
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

    /** ✅ 방 생성 */
    public Long createRoom(TeamRoomCreateRequest request, Long userId) {
        User user = getUser(userId);

        StudyRoom room;
        if (request.getRoomType() == RoomType.QUIZ) {
            room = QuizRoom.builder()
                    .title(request.getTitle())
                    .password(request.getPassword())
                    .inviteCode(generateInviteCode())
                    .roomType(RoomType.QUIZ)
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
                    .roomType(RoomType.FOCUS)
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

    /** ✅ 방 입장 */
    public void enterRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);

        boolean alreadyIn = participantRepository.existsByRoomAndUser(room, user);
        if (!alreadyIn) {
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

    /** ✅ 방 퇴장 */
    public void leaveRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);

        StudyRoomParticipant participant = participantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("방 참가자가 아닙니다."));
        participantRepository.delete(participant);
    }

    /** ✅ 방 삭제 (종료) */
    public void deleteRoom(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);

        // 방장만 삭제 가능
        StudyRoomParticipant host = participantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("방 참가자가 아닙니다."));
        if (!host.isHost()) throw new IllegalArgumentException("방장만 삭제할 수 있습니다.");

        // 연관 엔티티 cascade 제거
        participantRepository.deleteAllByRoom(room);
        if (room instanceof QuizRoom) {
            quizRoomRepository.delete((QuizRoom) room);
        } else if (room instanceof FocusRoom) {
            focusRoomRepository.delete((FocusRoom) room);
        } else {
            studyRoomRepository.delete(room); // fallback
        }

        // Redis 로그 삭제
        redisService.deleteRoomLogs(roomId);
    }

    /** ✅ 방 상세 조회 */
    public TeamRoomDetailResponse getRoomDetail(Long roomId, Long userId) {
        StudyRoom room = getRoom(roomId);
        User user = getUser(userId);
        List<StudyRoomParticipant> participants = participantRepository.findAllByRoom(room);
        return TeamRoomDetailResponse.of(room, participants);
    }

    /** ✅ 나의 방 리스트 */
    public TeamRoomListResponse getMyRoomList(Long userId) {
        User user = getUser(userId);
        List<StudyRoomParticipant> participation = participantRepository.findAllByUser(user);
        return TeamRoomListResponse.of(participation);
    }

    // =========================
    // 🔧 내부 유틸 메서드
    // =========================

    private String generateInviteCode() {
        return "C" + System.currentTimeMillis(); // TODO: 임시 로직, 추후 UUID 등 대체
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private StudyRoom getRoom(Long roomId) {
        return studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 방이 존재하지 않습니다."));
    }
}
