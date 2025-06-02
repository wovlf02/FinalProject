package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import com.hamcam.back.dto.study.team.socket.request.*;
import com.hamcam.back.dto.study.team.socket.response.FileUploadNoticeResponse;
import com.hamcam.back.dto.study.team.socket.response.VoteResultResponse;
import com.hamcam.back.service.livekit.LiveKitService;
import com.hamcam.back.service.study.team.socket.QuizRoomSocketService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuizRoomSocketController {

    private final QuizRoomSocketService quizRoomSocketService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LiveKitService liveKitService;

    // ✅ 세션에서 userId 추출 유틸
    private Long extractUserId(HttpServletRequest request) {
        return SessionUtil.getUserId(request);
    }

    /**
     * ✅ REST 방식 토큰 발급 API (LiveKit 접속용)
     */
    @GetMapping("/livekit-token")
    public ResponseEntity<LiveKitTokenResponse> getLivekitToken(
            @RequestParam String roomName,
            @RequestParam(defaultValue = "false") boolean isPresenter,
            HttpServletRequest httpRequest
    ) {
        Long userId = extractUserId(httpRequest);
        LiveKitTokenResponse response = liveKitService.issueTokenResponse(userId.toString(), roomName, isPresenter);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 준비 상태 전달
     */
    @MessageMapping("/quiz/ready")
    public void ready(HttpServletRequest request, RoomReadyRequest dto) {
        quizRoomSocketService.setReady(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 문제풀이 시작 요청
     */
    @MessageMapping("/quiz/start")
    public void startProblem(HttpServletRequest request, RoomStartRequest dto) {
        quizRoomSocketService.startProblem(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 손들기 - 발표자 후보 등록
     */
    @MessageMapping("/quiz/hand")
    public void raiseHand(HttpServletRequest request, RoomHandRequest dto) {
        quizRoomSocketService.raiseHand(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 발표자 선정 알림
     */
    @MessageMapping("/quiz/announce")
    public void announcePresenter(HttpServletRequest request, PresenterAnnounceRequest dto) {
        quizRoomSocketService.announcePresenter(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 발표 종료 처리
     */
    @MessageMapping("/quiz/end-presentation")
    public void endPresentation(HttpServletRequest request, RoomEndPresentationRequest dto) {
        quizRoomSocketService.endPresentation(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 투표 제출 및 결과 전송
     */
    @MessageMapping("/quiz/vote")
    public void submitVote(HttpServletRequest request, VoteSubmitRequest dto) {
        Long userId = extractUserId(request);
        VoteResultResponse result = quizRoomSocketService.submitVote(dto.getRoomId(), userId, dto.getVote());
        if (result != null) {
            messagingTemplate.convertAndSend("/sub/quiz/room/" + dto.getRoomId(), result);
        }
    }

    /**
     * ✅ 다음 문제풀이 요청
     */
    @MessageMapping("/quiz/continue")
    public void continueQuiz(HttpServletRequest request, RoomContinueRequest dto) {
        quizRoomSocketService.continueQuiz(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 문제풀이 종료 요청
     */
    @MessageMapping("/quiz/terminate")
    public void terminateRoom(HttpServletRequest request, RoomTerminateRequest dto) {
        quizRoomSocketService.terminateRoom(dto.getRoomId(), extractUserId(request));
    }

    /**
     * ✅ 채팅 전송
     */
    @MessageMapping("/quiz/chat/send")
    public void sendChatMessage(HttpServletRequest request, ChatMessageRequest dto) {
        Long userId = extractUserId(request);
        ChatMessageResponse response = quizRoomSocketService.handleChatMessage(dto, userId);
        messagingTemplate.convertAndSend("/sub/quiz/room/" + dto.getRoomId(), response);
    }

    /**
     * ✅ 파일 업로드 완료 알림
     */
    @MessageMapping("/quiz/file/uploaded")
    public void notifyFileUploaded(HttpServletRequest request, FileUploadNoticeRequest dto) {
        Long userId = extractUserId(request);
        FileUploadNoticeResponse response = quizRoomSocketService.notifyFileUploaded(dto, userId);
        messagingTemplate.convertAndSend("/sub/quiz/room/" + dto.getRoomId(), response);
    }
}
