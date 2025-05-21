package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * [StompChatController]
 * STOMP 기반 WebSocket 채팅 메시지를 처리하는 컨트롤러 (보안 제거 + 확장)
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
     * 해당 채팅방의 구독자에게 /sub/chat/room/{roomId} 로 브로드캐스트
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload @Valid ChatMessageRequest messageRequest) {
        Long userId = messageRequest.getUserId(); // ✅ 프론트에서 전달

        log.info("📥 WebSocket 메시지 수신: roomId={}, userId={}", messageRequest.getRoomId(), userId);

        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest, userId);
        chatReadService.markReadAsUserId(response.getRoomId(), response.getMessageId(), userId);

        messagingTemplate.convertAndSend("/sub/chat/room/" + response.getRoomId(), response);
    }

    /**
     * 클라이언트가 /pub/chat/read 로 읽음 요청을 보내면
     * 서버는 읽음 처리 후 READ_ACK 메시지를 브로드캐스트
     */
    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload @Valid ChatReadRequest request) {
        Long userId = request.getUserId(); // ✅ 프론트에서 전달

        log.info("📖 읽음 요청 수신: userId={}, roomId={}, messageId={}", userId, request.getRoomId(), request.getMessageId());

        int unreadCount = chatReadService.markReadAsUserId(request.getRoomId(), request.getMessageId(), userId);

        ChatMessageResponse ack = ChatMessageResponse.builder()
                .type("READ_ACK")
                .messageId(request.getMessageId())
                .unreadCount(unreadCount)
                .roomId(request.getRoomId())
                .build();

        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId(), ack);
        log.info("✅ READ_ACK 브로드캐스트 완료: messageId={}, unreadCount={}", request.getMessageId(), unreadCount);
    }
}
