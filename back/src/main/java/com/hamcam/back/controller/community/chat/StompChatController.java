package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.dto.study.team.socket.request.FocusChatMessageRequest;
import com.hamcam.back.dto.study.team.socket.response.FocusChatMessageResponse;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import com.hamcam.back.service.study.team.chat.FocusChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * [StompChatController]
 * WebSocket 기반 실시간 채팅 메시지 처리 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final WebSocketChatService webSocketChatService;
    private final ChatReadService chatReadService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FocusChatService focusChatService;

    private static final String CHAT_DEST_PREFIX = "/sub/chat/room/";
    private static final String FOCUS_CHAT_DEST_PREFIX = "/sub/focus/room/";

    /**
     * ✅ 커뮤니티 일반 채팅 메시지 수신 및 전송
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload ChatMessageRequest messageRequest,
                                  SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserIdFromSession(accessor);
        log.info("📥 [채팅 수신] roomId={}, userId={}, type={}, content={}",
                messageRequest.getRoomId(), userId, messageRequest.getType(), messageRequest.getContent());

        if (messageRequest.getType() == ChatMessageType.READ_ACK) {
            log.warn("🚫 READ_ACK는 /chat/send 경로로 보낼 수 없습니다.");
            return;
        }

        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest, userId);
        chatReadService.markReadAsUserId(response.getRoomId(), response.getMessageId(), userId);

        messagingTemplate.convertAndSend(CHAT_DEST_PREFIX + response.getRoomId(), response);
    }

    /**
     * ✅ 커뮤니티 채팅 읽음 처리
     */
    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload ChatReadRequest request,
                                  SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserIdFromSession(accessor);
        Long roomId = request.getRoomId();
        Long messageId = request.getMessageId();

        log.info("📖 [읽음 처리 요청] roomId={}, messageId={}, userId={}", roomId, messageId, userId);

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
     * ✅ FocusRoom 채팅 수신 및 브로드캐스트
     */
    @MessageMapping("/focus/chat/{roomId}")
    public void handleFocusChat(@DestinationVariable Long roomId,
                                @Payload FocusChatMessageRequest message,
                                SimpMessageHeaderAccessor accessor) {
        Long senderId = extractUserIdFromSession(accessor);
        log.info("📥 [FocusChat] roomId={}, senderId={}, content={}", roomId, senderId, message.getContent());

        FocusChatMessageResponse response = focusChatService.saveAndBuild(roomId, senderId, message.getContent());

        messagingTemplate.convertAndSend(FOCUS_CHAT_DEST_PREFIX + roomId + "/chat", response);
    }

    /**
     * ✅ 세션에서 userId 추출
     */
    private Long extractUserIdFromSession(SimpMessageHeaderAccessor accessor) {
        Object userIdAttr = accessor.getSessionAttributes().get("userId");

        if (userIdAttr instanceof Long userId) {
            return userId;
        } else if (userIdAttr instanceof Integer intId) {
            return Long.valueOf(intId);
        } else if (userIdAttr instanceof String strId) {
            try {
                return Long.parseLong(strId);
            } catch (NumberFormatException e) {
                log.warn("userId 세션 파싱 실패: {}", strId);
            }
        }

        throw new IllegalArgumentException("세션에서 userId를 가져올 수 없습니다.");
    }
}
