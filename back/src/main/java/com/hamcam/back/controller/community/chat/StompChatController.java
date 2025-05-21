package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.service.community.chat.ChatReadService;
import com.hamcam.back.service.community.chat.WebSocketChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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
    public void handleChatMessage(@Payload @Valid ChatMessageRequest messageRequest, Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);

        log.info("📥 WebSocket 메시지 수신: roomId={}, userId={}", messageRequest.getRoomId(), userId);

        // 1. 메시지 저장
        ChatMessageResponse response = webSocketChatService.saveMessage(messageRequest, userId);

        // 2. 보낸 사람 기준 읽음 처리
        chatReadService.markReadAsUserId(response.getRoomId(), response.getMessageId(), userId);

        // 3. 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + response.getRoomId(), response);
    }

    /**
     * 클라이언트가 /pub/chat/read 로 읽음 요청을 보내면
     * 서버는 읽음 처리 후 READ_ACK 메시지를 브로드캐스트합니다.
     */
    @MessageMapping("/chat/read")
    public void handleReadMessage(@Payload @Valid ChatReadRequest request, Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);

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

    /**
     * WebSocket 연결 시 전달된 Principal에서 userId 추출
     */
    private Long extractUserIdFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            Object principalObj = token.getPrincipal();

            if (principalObj instanceof Long id) {
                return id;
            } else if (principalObj instanceof String str && str.matches("\\d+")) {
                return Long.parseLong(str);
            }
        }

        log.warn("❌ Principal에서 userId 추출 실패: {}", principal);
        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
