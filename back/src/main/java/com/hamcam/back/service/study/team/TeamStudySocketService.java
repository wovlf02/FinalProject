package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.socket.request.*;
import com.hamcam.back.dto.study.team.socket.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.StudyRoom;
import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.StudyRoomParticipantRepository;
import com.hamcam.back.repository.study.StudyRoomRepository;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamStudySocketService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;

    /** ✅ 실시간 손들기 큐 (roomId → Queue of userId) */
    private final Map<Long, Queue<Long>> handRaiseQueueMap = new ConcurrentHashMap<>();

    /** ✅ 손들기 요청 처리 */
    public void raiseHand(HandRaiseRequest request) {
        handRaiseQueueMap.putIfAbsent(request.getRoomId(), new LinkedList<>());
        handRaiseQueueMap.get(request.getRoomId()).add(request.getUserId());
    }

    /** ✅ 발표자 자동 선정 */
    public Optional<PresenterSelectedResponse> selectNextPresenter(Long roomId) {
        Queue<Long> queue = handRaiseQueueMap.getOrDefault(roomId, new LinkedList<>());
        if (queue.isEmpty()) return Optional.empty();

        Long userId = queue.poll(); // FIFO
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return Optional.of(PresenterSelectedResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .build());
    }

    /** ✅ 발표자 수동 선정 */
    public PresenterSelectedResponse manuallySelectPresenter(PresenterSelectRequest request) {
        User user = userRepository.findById(request.getSelectedUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return PresenterSelectedResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    /** ✅ 발표 종료 처리 */
    public void handleAnnouncementEnd(AnnouncementEndRequest request) {
        // 이후 투표 UI broadcast → WebSocket handler에서 emit 처리
        System.out.println("발표 종료됨 - 발표자 ID: " + request.getPresenterId());
    }

    /** ✅ 참가자 입장 broadcast 정보 생성 */
    public NewParticipantResponse createParticipantResponse(Long roomId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        StudyRoomParticipant participant = participantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTICIPANT));

        return NewParticipantResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .isHost(participant.isHost())
                .build();
    }

    /** ✅ 손들기 초기화 (방 종료 or 문제 전환 시) */
    public void clearHandRaiseQueue(Long roomId) {
        handRaiseQueueMap.remove(roomId);
    }
}
