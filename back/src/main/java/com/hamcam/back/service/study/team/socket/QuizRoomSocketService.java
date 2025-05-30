package com.hamcam.back.service.study.team.socket;

import com.hamcam.back.dto.study.team.socket.request.VoteType;
import com.hamcam.back.dto.study.team.socket.response.VoteResultResponse;
import com.hamcam.back.entity.study.team.QuizRoom;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.QuizRoomRepository;
import com.hamcam.back.repository.study.StudyRoomParticipantRepository;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuizRoomSocketService {

    private final QuizRoomRepository quizRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;

    // ✅ 발표 후보자 저장: roomId → userId 순서대로 저장
    private final Map<Long, Queue<Long>> handRaisedQueue = new ConcurrentHashMap<>();

    // ✅ 발표자 지정: roomId → 발표자 userId
    private final Map<Long, Long> presenterMap = new ConcurrentHashMap<>();

    // ✅ 투표 현황 저장: roomId → userId → VoteType
    private final Map<Long, Map<Long, VoteType>> voteMap = new ConcurrentHashMap<>();

    /**
     * ✅ 방 입장 처리
     */
    public void enterRoom(Long roomId, Long userId) {
        log.info("User {} entered quiz room {}", userId, roomId);
    }

    /**
     * ✅ 준비 처리 (추후 확장 가능)
     */
    public void setReady(Long roomId, Long userId) {
        log.info("User {} is ready in room {}", userId, roomId);
    }

    /**
     * ✅ 문제 시작 처리
     */
    public void startProblem(Long roomId, Long userId) {
        validateHost(roomId, userId);
        log.info("문제 시작됨: room {}", roomId);

        // 초기화
        handRaisedQueue.put(roomId, new LinkedList<>());
        presenterMap.remove(roomId);
        voteMap.remove(roomId);
    }

    /**
     * ✅ 손들기 처리
     */
    public void raiseHand(Long roomId, Long userId) {
        handRaisedQueue.putIfAbsent(roomId, new LinkedList<>());
        Queue<Long> queue = handRaisedQueue.get(roomId);

        if (!queue.contains(userId)) {
            queue.offer(userId);
            log.info("User {} raised hand in room {}", userId, roomId);
        }
    }

    /**
     * ✅ 발표자 선정 처리
     */
    public void announcePresenter(Long roomId, Long requesterId) {
        validateHost(roomId, requesterId);

        Queue<Long> queue = handRaisedQueue.getOrDefault(roomId, new LinkedList<>());
        Long nextPresenter = queue.poll();

        if (nextPresenter != null) {
            presenterMap.put(roomId, nextPresenter);
            log.info("발표자 선정: user {} in room {}", nextPresenter, roomId);
        } else {
            log.warn("No presenter candidate in room {}", roomId);
        }
    }

    /**
     * ✅ 발표 종료 → 투표 준비
     */
    public void endPresentation(Long roomId, Long userId) {
        log.info("발표 종료: room {}", roomId);
        voteMap.put(roomId, new HashMap<>());
    }

    /**
     * ✅ 투표 처리
     */
    public VoteResultResponse submitVote(Long roomId, Long userId, VoteType vote) {
        Map<Long, VoteType> votes = voteMap.get(roomId);
        if (votes == null) {
            throw new CustomException(ErrorCode.INVALID_OPERATION);
        }

        votes.put(userId, vote);

        int totalParticipants = participantRepository.countByStudyRoomId(roomId);
        if (votes.size() >= totalParticipants) {
            // 투표 집계
            int yesVotes = (int) votes.values().stream().filter(v -> v == VoteType.SUCCESS).count();
            int noVotes = votes.size() - yesVotes;
            boolean success = yesVotes > totalParticipants / 2;

            voteMap.remove(roomId);

            return VoteResultResponse.builder()
                    .roomId(roomId)
                    .success(success)
                    .yesVotes(yesVotes)
                    .noVotes(noVotes)
                    .totalParticipants(totalParticipants)
                    .build();
        }

        return null;
    }

    /**
     * ✅ 다음 문제 계속
     */
    public void continueQuiz(Long roomId, Long userId) {
        validateHost(roomId, userId);
        log.info("방 {}: 문제풀이 계속 진행", roomId);
    }

    /**
     * ✅ 방 종료
     */
    public void terminateRoom(Long roomId, Long userId) {
        validateHost(roomId, userId);
        handRaisedQueue.remove(roomId);
        presenterMap.remove(roomId);
        voteMap.remove(roomId);
        log.info("방 종료 및 메모리 정리 완료: room {}", roomId);
    }

    /**
     * ✅ 방장이 맞는지 검증
     */
    private void validateHost(Long roomId, Long userId) {
        QuizRoom room = quizRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        // 1. 참가자인지 먼저 확인
        if (!participantRepository.existsByStudyRoomIdAndUserId(roomId, userId)) {
            throw new CustomException(ErrorCode.USER_NOT_PARTICIPANT);
        }

        // 2. 방장인지 확인
        if (!room.getHost().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_HOST);
        }
    }

}
