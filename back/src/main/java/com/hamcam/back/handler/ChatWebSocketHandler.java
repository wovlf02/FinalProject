package com.hamcam.back.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.community.chat.ChatMessageService;
import com.hamcam.back.service.community.chat.ChatReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final ChatReadService chatReadService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, User> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromCookie(session);
        if (token == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("JWT 토큰이 누락되었습니다."));
            return;
        }

        try {
            if (!jwtProvider.validateTokenWithoutRedis(token)) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("JWT 토큰이 유효하지 않습니다."));
                return;
            }

            Long userId = jwtProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("사용자 조회 실패"));

            sessionUserMap.put(session.getId(), user);
            log.info("🔌 WebSocket 연결됨 - 세션 ID: {}, 사용자: {} (ID: {})", session.getId(), user.getUsername(), user.getId());

        } catch (Exception e) {
            log.error("❌ WebSocket 인증 실패: {}", e.getMessage());
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("WebSocket 인증 실패"));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            log.info("📥 수신 메시지: {}", message.getPayload());

            Map<String, Object> jsonMap = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) jsonMap.get("type");
            User user = sessionUserMap.get(session.getId());

            if (user == null) throw new IllegalStateException("인증된 사용자 없음");

            if ("ENTER".equalsIgnoreCase(type)) {
                Long roomId = Long.valueOf(jsonMap.get("roomId").toString());
                roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
                sessionRoomMap.put(session.getId(), roomId);
                chatReadService.updateLastReadMessage(roomId, user.getId());
                log.info("🚪 입장 - 사용자 {} / 방 {}", user.getId(), roomId);
                return;
            }

            if ("READ".equalsIgnoreCase(type)) {
                ChatReadRequest readRequest = objectMapper.convertValue(jsonMap, ChatReadRequest.class);
                Long roomId = readRequest.getRoomId();
                Long messageId = readRequest.getMessageId();

                int unreadCount = chatReadService.markAsRead(user, roomId, messageId);

                Map<String, Object> readAck = new HashMap<>();
                readAck.put("type", "READ_ACK");
                readAck.put("roomId", roomId);
                readAck.put("messageId", messageId);
                readAck.put("unreadCount", unreadCount);

                String ackPayload = objectMapper.writeValueAsString(readAck);
                for (WebSocketSession s : roomSessions.getOrDefault(roomId, Set.of())) {
                    if (s.isOpen()) s.sendMessage(new TextMessage(ackPayload));
                }
                return;
            }

            // 일반 메시지
            ChatMessageRequest request = objectMapper.convertValue(jsonMap, ChatMessageRequest.class);
            Long roomId = request.getRoomId();
            ChatMessageResponse savedMessage = chatMessageService.sendMessage(roomId, user, request);

            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
            String payload = objectMapper.writeValueAsString(savedMessage);

            for (WebSocketSession s : roomSessions.getOrDefault(roomId, Set.of())) {
                if (s.isOpen()) s.sendMessage(new TextMessage(payload));
            }

        } catch (Exception e) {
            log.error("❌ WebSocket 메시지 처리 오류", e);
            try {
                session.sendMessage(new TextMessage("{\"type\":\"ERROR\",\"message\":\"메시지 처리 중 오류 발생\"}"));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionUserMap.remove(session.getId());
        Long roomId = sessionRoomMap.remove(session.getId());

        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }

        log.info("❎ WebSocket 연결 종료 - 세션: {}", session.getId());
    }

    /**
     * WebSocket 요청의 Cookie 헤더에서 accessToken 추출
     */
    private String getTokenFromCookie(WebSocketSession session) {
        List<String> cookies = session.getHandshakeHeaders().get("cookie");
        if (cookies != null) {
            for (String cookieHeader : cookies) {
                String[] cookiePairs = cookieHeader.split(";");
                for (String cookie : cookiePairs) {
                    String[] pair = cookie.trim().split("=");
                    if (pair.length == 2 && pair[0].equals("accessToken")) {
                        return pair[1];
                    }
                }
            }
        }
        return null;
    }
}
