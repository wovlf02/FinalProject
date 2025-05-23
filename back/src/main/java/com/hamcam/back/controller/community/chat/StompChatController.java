package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.chat.ChatMessageType;
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
 * WebSocket 기반 실시간 채팅 메시지 처리 컨트롤러
 * - 메시지 수신, 저장, 읽음 처리, 브로드캐스트 수행
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final WebSocketChatService webSocketChatService;
    private final ChatReadService chatReadService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String CHAT_DEST_PREFIX = "/sub/chat/room/";
    private static final String TYPE_READ_ACK = "READ_ACK";

    /**
     * ✅ 채팅 메시지 수신 및 전송
     * 클라이언트 → /pub/chat/send
     * 서버 → /sub/chat/room/{roomId}
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload @Valid ChatMessageRequest messageRequest) {
        Long userId = messageRequest.getUserId();
        log.info("📥 [채팅 수신] roomId={}, userId={}", messageRequest.getRoomId(), userId);

        // 메시지 저장 및 응답 생성
        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest, userId);

        // 읽음 처리 (자기 자신 기준)
        chatReadService.markReadAsUserId(response.getRoomId(), response.getMessageId(), userId);

        // 전송 (구독 경로)
        messagingTemplate.convertAndSend(CHAT_DEST_PREFIX + response.getRoomId(), response);
    }

    /**
     * ✅ 메시지 읽음 처리
     * 클라이언트 → /pub/chat/read
     * 서버 → /sub/chat/room/{roomId}
     */
    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload @Valid ChatReadRequest request) {
        Long userId = request.getUserId();
        Long roomId = request.getRoomId();
        Long messageId = request.getMessageId();

        log.info("📖 [읽음 처리] roomId={}, messageId={}, userId={}", roomId, messageId, userId);

        // DB에 읽음 상태 반영
        int unreadCount = chatReadService.markReadAsUserId(roomId, messageId, userId);

        // 읽음 알림 전송 (READ_ACK 메시지)
        ChatMessageResponse ack = ChatMessageResponse.builder()
                .type(ChatMessageType.READ_ACK)
                .roomId(roomId)
                .messageId(messageId)
                .unreadCount(unreadCount)
                .build();

        messagingTemplate.convertAndSend(CHAT_DEST_PREFIX + roomId, ack);
        log.info("✅ [READ_ACK 전송] messageId={}, unreadCount={}", messageId, unreadCount);
    }
}
