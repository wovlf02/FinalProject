package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * [StompChatController]
 * STOMP 기반 WebSocket 채팅 메시지를 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final WebSocketChatService webSocketChatService;
    private final ChatReadService chatReadService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트가 /pub/chat/send 로 메시지를 전송하면
     * 해당 채팅방의 구독자에게 /sub/chat/room/{roomId} 로 브로드캐스트합니다.
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload ChatMessageRequest messageRequest) {
        // 1. 메시지 저장
        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest);

        // 2. 읽음 처리 (보낸 사람 기준)
        chatReadService.markReadAsAuthenticatedUser(response.getRoomId(), response.getMessageId());

        // 3. 구독 중인 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + response.getRoomId(),
                response
        );
    }
}
