package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import com.hamcam.back.util.SessionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

    /**
     * ✅ 채팅 메시지 수신 및 전송
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload @Valid ChatMessageRequest messageRequest,
                                  SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserIdFromSession(accessor);
        log.info("📥 [채팅 수신] roomId={}, userId={}", messageRequest.getRoomId(), userId);

        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest, userId);
        chatReadService.markReadAsUserId(response.getRoomId(), response.getMessageId(), userId);
        messagingTemplate.convertAndSend(CHAT_DEST_PREFIX + response.getRoomId(), response);
    }

    /**
     * ✅ 메시지 읽음 처리
     */
    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload @Valid ChatReadRequest request,
                                  SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserIdFromSession(accessor);
        Long roomId = request.getRoomId();
        Long messageId = request.getMessageId();

        log.info("📖 [읽음 처리] roomId={}, messageId={}, userId={}", roomId, messageId, userId);

        int unreadCount = chatReadService.markReadAsUserId(roomId, messageId, userId);

        ChatMessageResponse ack = ChatMessageResponse.builder()
                .type(ChatMessageType.READ_ACK)
                .roomId(roomId)
                .messageId(messageId)
                .unreadCount(unreadCount)
                .build();

        messagingTemplate.convertAndSend(CHAT_DEST_PREFIX + roomId, ack);
        log.info("✅ [READ_ACK 전송] messageId={}, unreadCount={}", messageId, unreadCount);
    }

    /**
     * ✅ 세션에서 userId 추출
     */
    private Long extractUserIdFromSession(SimpMessageHeaderAccessor accessor) {
        Object session = accessor.getSessionAttributes().get("HTTP.SESSION.ID");
        if (session instanceof jakarta.servlet.http.HttpSession httpSession) {
            Object userIdAttr = httpSession.getAttribute("userId");
            if (userIdAttr instanceof Long userId) {
                return userId;
            }
        }
        throw new IllegalArgumentException("세션에서 userId를 가져올 수 없습니다.");
    }
}
