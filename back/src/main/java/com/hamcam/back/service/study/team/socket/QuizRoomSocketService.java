package com.hamcam.back.service.study.team.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.dto.study.team.socket.request.FileUploadNoticeRequest;
import com.hamcam.back.dto.study.team.socket.request.VoteType;
import com.hamcam.back.dto.study.team.socket.response.*;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.study.team.Problem;
import com.hamcam.back.entity.study.team.QuizRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.ProblemRepository;
import com.hamcam.back.repository.study.QuizRoomRepository;
import com.hamcam.back.repository.study.StudyRoomParticipantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProblemRepository  problemRepository;

    private static final String CHAT_KEY_PREFIX = "quiz:%s:chat";

    // 발표 후보자 저장: roomId → userId 순서대로 저장
    private final Map<Long, Queue<Long>> handRaisedQueue = new ConcurrentHashMap<>();

    // 발표자 지정: roomId → 발표자 userId
    private final Map<Long, Long> presenterMap = new ConcurrentHashMap<>();

    // 투표 현황 저장: roomId → userId → VoteType
    private final Map<Long, Map<Long, VoteType>> voteMap = new ConcurrentHashMap<>();

    // 랭킹 저장용 Map
    private final Map<Long, List<String>> correctUserRankingMap = new ConcurrentHashMap<>();

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
        correctUserRankingMap.remove(roomId); // ✅ 정답자 랭킹 초기화
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
     * ✅ 발표 종료 → 투표 준비 및 투표 UI 브로드캐스트
     */
    public void endPresentation(Long roomId, Long userId) {
        log.info("발표 종료: room {}", roomId);

        // 투표 맵 초기화
        voteMap.put(roomId, new HashMap<>());

        // 투표 UI 띄우기 신호 전송
        messagingTemplate.convertAndSend(
                "/sub/quiz/room/" + roomId,
                new VoteUITriggerResponse("SHOW_VOTE_UI")
        );
    }

    /**
     * ✅ 투표 처리 + 결과 브로드캐스트
     */
    public VoteResultResponse submitVote(Long roomId, Long userId, VoteType vote) {
        Map<Long, VoteType> votes = voteMap.get(roomId);
        if (votes == null) {
            throw new CustomException(ErrorCode.INVALID_OPERATION);
        }

        votes.put(userId, vote);

        int totalParticipants = participantRepository.countByStudyRoomId(roomId);
        if (votes.size() >= totalParticipants) {
            int yesVotes = (int) votes.values().stream().filter(v -> v == VoteType.SUCCESS).count();
            int noVotes = votes.size() - yesVotes;
            boolean success = yesVotes > totalParticipants / 2;

            voteMap.remove(roomId);

            VoteResultResponse result = VoteResultResponse.builder()
                    .roomId(roomId)
                    .success(success)
                    .yesVotes(yesVotes)
                    .noVotes(noVotes)
                    .totalParticipants(totalParticipants)
                    .build();

            // ✅ 결과 전송
            messagingTemplate.convertAndSend("/sub/quiz/room/" + roomId, result);

            // ✅ 안내 메시지 전송
            String message = success
                    ? "🎉 발표가 성공적으로 완료되었습니다!"
                    : "❌ 발표에 실패했습니다. 다음 문제로 넘어가세요!";
            messagingTemplate.convertAndSend("/sub/quiz/room/" + roomId, new TextNoticeResponse(message));

            return result;
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

        if (!participantRepository.existsByStudyRoomIdAndUserId(roomId, userId)) {
            throw new CustomException(ErrorCode.USER_NOT_PARTICIPANT);
        }

        if (!room.getHost().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_HOST);
        }
    }

    /**
     * ✅ 채팅 메시지 처리: Redis 저장 + WebSocket 브로드캐스트
     */
    public void handleAndBroadcastChatMessage(String roomType, Long roomId, Long userId, String content) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String roomKey = roomType + "-" + roomId;

        StudyChatMessageResponse response = StudyChatMessageResponse.builder()
                .roomId(roomKey)
                .senderId(userId)
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImageUrl())
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        try {
            String key = String.format("study:%s:chat", roomKey);
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForList().rightPush(key, json);
        } catch (Exception e) {
            log.error("❌ Redis 채팅 저장 실패", e);
        }

        messagingTemplate.convertAndSend("/sub/" + roomType + "/room/" + roomId, response);

        log.info("💬 [{}] 채팅 메시지 from {}: {}", roomKey, user.getNickname(), content);
    }



    /**
     * ✅ 파일 업로드 완료 알림 생성
     */
    public FileUploadNoticeResponse notifyFileUploaded(FileUploadNoticeRequest requestDto, Long userId) {
        String nickname = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getNickname();

        FileUploadNoticeResponse response = new FileUploadNoticeResponse(
                nickname,
                requestDto.getFileName(),
                requestDto.getFileUrl()
        );

        log.info("파일 업로드 완료: {} by {}", requestDto.getFileName(), nickname);
        return response;
    }

    /**
     * ✅ 정답 제출 처리
     */
    public void submitAnswer(Long roomId, Long problemId, Long userId, String nickname, String submittedAnswer) {
        // 1. 문제 조회
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

        String correctAnswer = problem.getAnswer().trim();
        String userInput = submittedAnswer.trim();

        // 2. 정답 비교
        boolean isCorrect = correctAnswer.equals(userInput);

        if (!isCorrect) {
            log.info("❌ 오답 제출: {} (제출: '{}', 정답: '{}')", nickname, userInput, correctAnswer);

            // 오답도 알림은 보내되, data.correct = false 포함
            messagingTemplate.convertAndSend(
                    "/sub/quiz/room/" + roomId,
                    MessageResponse.of(nickname + "님이 정답을 맞추셨습니다!", Map.of("correct", false, "nickname", nickname))
            );
            return;
        }

        // 3. 정답자 랭킹 리스트 초기화
        correctUserRankingMap.putIfAbsent(roomId, new ArrayList<>());
        List<String> ranking = correctUserRankingMap.get(roomId);

        // 4. 중복 정답 방지
        if (ranking.contains(nickname)) {
            log.info("⚠️ 이미 정답 맞춘 사용자: {}", nickname);
            return;
        }

        // 5. 랭킹에 추가
        ranking.add(nickname);
        log.info("✅ 정답자 등록: {} (room {})", nickname, roomId);

        // 6. 랭킹 브로드캐스트
        List<RankingDto> dtoList = new ArrayList<>();
        for (int i = 0; i < ranking.size(); i++) {
            dtoList.add(new RankingDto(i + 1, ranking.get(i)));
        }

        messagingTemplate.convertAndSend("/sub/quiz/room/" + roomId + "/ranking", dtoList);

        // 7. 정답자 메시지 전송 (data에 correct 포함)
        messagingTemplate.convertAndSend(
                "/sub/quiz/room/" + roomId,
                MessageResponse.of(nickname + "님이 정답을 맞추셨습니다!", Map.of("correct", true, "nickname", nickname))
        );
    }

    /**
     * ✅ 문제 변경 시 상태 초기화 처리
     */
    public void changeProblem(Long roomId, Long problemId) {
        log.info("🔄 문제 전환: roomId={}, problemId={}", roomId, problemId);

        // 1. 정답자 랭킹 초기화
        correctUserRankingMap.put(roomId, new ArrayList<>());

        // ✅ 랭킹 초기화 브로드캐스트 추가
        messagingTemplate.convertAndSend("/sub/quiz/room/" + roomId + "/ranking", new ArrayList<>());

        // 2. 발표자, 손들기, 투표 상태 초기화
        handRaisedQueue.remove(roomId);
        presenterMap.remove(roomId);
        voteMap.remove(roomId);

        // 3. 문제 변경 공지
        messagingTemplate.convertAndSend(
                "/sub/quiz/room/" + roomId,
                new TextNoticeResponse("🧠 새로운 문제가 선택되었습니다! 모두 도전해보세요.")
        );
    }


}
