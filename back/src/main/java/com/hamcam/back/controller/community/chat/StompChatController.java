package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.dto.study.team.socket.request.FocusChatMessageRequest;
import com.hamcam.back.dto.study.team.socket.request.StudyChatMessageRequest;
import com.hamcam.back.dto.study.team.socket.response.FocusChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import com.hamcam.back.service.study.team.chat.StudyChatService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final WebSocketChatService webSocketChatService;
    private final ChatReadService chatReadService;
    private final SimpMessagingTemplate messagingTemplate;
    private final StudyChatService studyChatService;
    private final UserRepository userRepository;

    private static final String CHAT_DEST_PREFIX = "/sub/chat/room/";
    private static final String FOCUS_CHAT_DEST_PREFIX = "/sub/focus/room/";

    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload ChatMessageRequest messageRequest,
                                  SimpMessageHeaderAccessor accessor) {
        User user = extractUserFromSession(accessor);
        Long userId = user.getId();

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

    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload ChatReadRequest request,
                                  SimpMessageHeaderAccessor accessor) {
        User user = extractUserFromSession(accessor);
        Long userId = user.getId();
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

    @MessageMapping("/focus/chat/{roomId}")
    public void handleFocusChat(@DestinationVariable Long roomId,
                                @Payload StudyChatMessageRequest message,
                                SimpMessageHeaderAccessor accessor) {

        Long userId = SessionUtil.getUserIdFromSession(accessor.getSessionAttributes());

        log.info("📥 [FocusChat] userId={}, roomId={}, content={}", userId, roomId, message.getContent());

        // ✅ 채팅 처리
        studyChatService.handleAndBroadcastChatMessage("focus", roomId, userId, message.getContent());
    }


    /**
     * ✅ WebSocket 세션에서 userId를 꺼내 User 조회
     */
    private User extractUserFromSession(SimpMessageHeaderAccessor accessor) {
        Object userIdAttr = accessor.getSessionAttributes().get("userId");

        Long userId = null;
        if (userIdAttr instanceof Long l) {
            userId = l;
        } else if (userIdAttr instanceof Integer i) {
            userId = i.longValue();
        } else if (userIdAttr instanceof String s) {
            try {
                userId = Long.parseLong(s);
            } catch (NumberFormatException e) {
                log.warn("userId 파싱 실패: {}", s);
            }
        }

        if (userId == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private HttpServletRequest getHttpRequestFromAccessor(SimpMessageHeaderAccessor accessor) {
        Object req = accessor.getSessionAttributes().get("HTTP_REQUEST");
        if (req instanceof HttpServletRequest request) {
            return request;
        }
        throw new IllegalArgumentException("HttpServletRequest를 세션에서 가져올 수 없습니다.");
    }

}
