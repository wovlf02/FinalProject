package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.socket.request.StudyChatMessageRequest;
import com.hamcam.back.service.study.team.chat.StudyChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.hamcam.back.util.SessionUtil;

/**
 * ✅ 팀 스터디(QuizRoom/FocusRoom) 채팅 전용 WebSocket 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class StudyChatSocketController {

    private final StudyChatService studyChatService;

    @MessageMapping("/quiz/chat/send")
    public void handleQuizChat(StudyChatMessageRequest dto, SimpMessageHeaderAccessor accessor) {
        Long userId = SessionUtil.getUserIdFromSession(accessor.getSessionAttributes());
        studyChatService.handleAndBroadcastChatMessage("quiz", dto.getRoomId(), userId, dto.getContent());
    }

    @MessageMapping("/focus/chat/send")
    public void handleFocusChat(StudyChatMessageRequest dto, SimpMessageHeaderAccessor accessor) {
        Long userId = SessionUtil.getUserIdFromSession(accessor.getSessionAttributes());
        studyChatService.handleAndBroadcastChatMessage("focus", dto.getRoomId(), userId, dto.getContent());
    }

}
