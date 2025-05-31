package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.dto.study.team.socket.request.*;
import com.hamcam.back.dto.study.team.socket.response.FileUploadNoticeResponse;
import com.hamcam.back.dto.study.team.socket.response.VoteResultResponse;
import com.hamcam.back.service.study.team.socket.QuizRoomSocketService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuizRoomSocketController {

    private final QuizRoomSocketService quizRoomSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ✅ 방 입장 시 처리
     */
    @MessageMapping("/quiz/enter")
    public void enterRoom(HttpServletRequest request, RoomEnterRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.enterRoom(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 준비 상태 전달
     */
    @MessageMapping("/quiz/ready")
    public void ready(HttpServletRequest request, RoomReadyRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.setReady(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 문제풀이 시작
     */
    @MessageMapping("/quiz/start")
    public void startProblem(HttpServletRequest request, RoomStartRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.startProblem(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 손들기 (발표자 후보)
     */
    @MessageMapping("/quiz/hand")
    public void raiseHand(HttpServletRequest request, RoomHandRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.raiseHand(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 발표자 알림 (서버에서 선정하여 브로드캐스트)
     */
    @MessageMapping("/quiz/announce")
    public void announcePresenter(HttpServletRequest request, PresenterAnnounceRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.announcePresenter(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 발표 종료
     */
    @MessageMapping("/quiz/end-presentation")
    public void endPresentation(HttpServletRequest request, RoomEndPresentationRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.endPresentation(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 투표 제출
     */
    @MessageMapping("/quiz/vote")
    public void submitVote(HttpServletRequest request, VoteSubmitRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        VoteResultResponse result = quizRoomSocketService.submitVote(requestDto.getRoomId(), userId, requestDto.getVote());
        if (result != null) {
            messagingTemplate.convertAndSend("/sub/quiz/room/" + requestDto.getRoomId(), result);
        }
    }

    /**
     * ✅ 다음 문제풀이 계속 진행
     */
    @MessageMapping("/quiz/continue")
    public void continueQuiz(HttpServletRequest request, RoomContinueRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.continueQuiz(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 문제풀이 종료
     */
    @MessageMapping("/quiz/terminate")
    public void terminateRoom(HttpServletRequest request, RoomTerminateRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        quizRoomSocketService.terminateRoom(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 실시간 채팅 메시지 송신
     */
    @MessageMapping("/quiz/chat/send")
    public void sendChatMessage(HttpServletRequest request, ChatMessageRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        ChatMessageResponse response = quizRoomSocketService.handleChatMessage(requestDto, userId);
        messagingTemplate.convertAndSend("/sub/quiz/room/" + requestDto.getRoomId(), response);
    }

    /**
     * ✅ 파일 업로드 완료 후 알림 전송
     */
    @MessageMapping("/quiz/file/uploaded")
    public void notifyFileUploaded(HttpServletRequest request, FileUploadNoticeRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        FileUploadNoticeResponse response = quizRoomSocketService.notifyFileUploaded(requestDto, userId);
        messagingTemplate.convertAndSend("/sub/quiz/room/" + requestDto.getRoomId(), response);
    }


}
