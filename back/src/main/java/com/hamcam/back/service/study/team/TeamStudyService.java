package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.rest.request.*;
import com.hamcam.back.dto.study.team.rest.response.TeamRoomDetailResponse;
import com.hamcam.back.dto.study.team.rest.response.inner.ParticipantInfo;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.*;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.util.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamStudyService {

    private final UserRepository userRepository;
    private final QuizRoomRepository quizRoomRepository;
    private final FocusRoomRepository focusRoomRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final RedisService redisService;

    /** ✅ 방 생성 (QuizRoom or FocusRoom) */
    public Long createRoom(TeamRoomCreateRequest request, Long userId) {
        User user = getUser(userId);
        StudyRoom room;

        if (request.getRoomType() == RoomType.QUIZ) {
            room = quizRoomRepository.save(QuizRoom.builder()
                    .title(request.getTitle())
                    .roomType(RoomType.QUIZ)
                    .subject(request.getSubject())
                    .grade(request.getGrade())
                    .month(request.getMonth())
                    .difficulty(request.getDifficulty())
                    .problemId(request.getProblemId())
                    .password(request.getPassword())
                    .build());
        } else {
            room = focusRoomRepository.save(FocusRoom.builder()
                    .title(request.getTitle())
                    .roomType(RoomType.FOCUS)
                    .targetTime(request.getTargetTime())
                    .password(request.getPassword())
                    .build());
        }

        room.generateInviteCode();

        participantRepository.save(StudyRoomParticipant.of(user, room, true)); // 방장 등록
        return room.getId();
    }

    /** ✅ 방 입장 */
    public void joinRoom(TeamRoomJoinRequest request, Long userId) {
        User user = getUser(userId);
        StudyRoom room = studyRoomRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isActive()) {
            throw new CustomException(ErrorCode.ROOM_ALREADY_CLOSED);
        }

        if (request.getPassword() != null && !request.getPassword().equals(room.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        if (participantRepository.existsByRoomAndUser(room, user)) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }

        participantRepository.save(StudyRoomParticipant.of(user, room, false));
    }

    /** ✅ 방 상세 조회 */
    public TeamRoomDetailResponse getRoomDetail(Long roomId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        List<StudyRoomParticipant> participants = participantRepository.findByRoom(room);

        List<ParticipantInfo> participantInfos = participants.stream()
                .map(p -> ParticipantInfo.from(p.getUser(), p.isHost()))
                .collect(Collectors.toList());

        return TeamRoomDetailResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .isActive(room.isActive())
                .inviteCode(room.getInviteCode())
                .participants(participantInfos)
                .build();
    }

    /** ✅ 방 종료 */
    public void finishRoom(Long roomId, Long userId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User user = getUser(userId);
        StudyRoomParticipant participant = participantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTICIPANT));

        if (!participant.isHost()) {
            throw new CustomException(ErrorCode.NOT_HOST);
        }

        room.deactivate(); // isActive → false
        redisService.deleteChatLog(roomId); // Redis 로그 삭제
    }

    /** ✅ 실패 시 게시글 자동 업로드 */
    public void uploadFailToCommunity(QuizFailUploadRequest request, Long userId) {
        // 간단 처리: 커뮤니티 서비스 호출 대신 로그 출력 or 게시글 저장소 호출
        // 실제 서비스라면 CommunityService API 호출 or DB 저장 필요
        System.out.println("업로드 요청됨 → 문제 ID: " + request.getProblemId());
        System.out.println("작성자 ID: " + userId + ", 내용: " + request.getContent());
        // TODO: 커뮤니티 게시글 등록 서비스 연동
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
