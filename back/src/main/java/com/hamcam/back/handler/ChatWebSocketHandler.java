package com.hamcam.back.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hamcam.back.config.auth.JwtProvider;
import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
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

@Component
@RequiredArgsConstructor
@Slf4j
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
        String token = getTokenFromQuery(session);
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
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            sessionUserMap.put(session.getId(), user);
            log.info("🔌 WebSocket 연결됨 - 세션 ID: {}, 사용자: {} (ID: {})", session.getId(), user.getUsername(), user.getId());

        } catch (Exception e) {
            log.error("❌ WebSocket 인증 실패: {}", e.getMessage());
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("WebSocket 인증 실패: " + e.getMessage()));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            log.info("📥 수신 메시지 payload: {}", message.getPayload());

            Map<String, Object> jsonMap = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) jsonMap.get("type");

            User user = sessionUserMap.get(session.getId());
            if (user == null) throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");

            // ✅ 방 입장 시 roomSessions 등록
            // ✅ 방 입장 시 roomSessions 등록 및 마지막 읽은 메시지 갱신
            if ("ENTER".equalsIgnoreCase(type)) {
                Long roomId = Long.valueOf(jsonMap.get("roomId").toString());
                roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
                sessionRoomMap.put(session.getId(), roomId);

                try {
                    chatReadService.updateLastReadMessage(roomId, user.getId());
                    log.info("📌 lastReadMessageId 갱신 완료 - 사용자: {}, 채팅방: {}", user.getId(), roomId);
                } catch (Exception e) {
                    log.warn("⚠️ lastReadMessageId 갱신 실패 - 사용자: {}, 채팅방: {}, 이유: {}", user.getId(), roomId, e.getMessage());
                }

                log.info("🚪 사용자 {}가 채팅방 {}에 입장", user.getId(), roomId);
                return;
            }


            // ✅ 읽음 처리
            if ("READ".equalsIgnoreCase(type)) {
                ChatReadRequest readRequest = objectMapper.convertValue(jsonMap, ChatReadRequest.class);
                Long roomId = readRequest.getRoomId();
                Long messageId = readRequest.getMessageId();

                int unreadCount = chatReadService.markAsRead(user, roomId, messageId);
                Map<String, Object> readResult = new HashMap<>();
                readResult.put("type", "READ_ACK");
                readResult.put("roomId", roomId);
                readResult.put("messageId", messageId);
                readResult.put("unreadCount", unreadCount);

                String payload = objectMapper.writeValueAsString(readResult);
                roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

                for (WebSocketSession s : roomSessions.get(roomId)) {
                    if (s.isOpen()) s.sendMessage(new TextMessage(payload));
                }

                log.info("✅ 읽음 처리 완료 - 사용자: {}, 채팅방: {}, 메시지: {}, 남은 미읽음: {}",
                        user.getId(), roomId, messageId, unreadCount);
                return;
            }

            // ✅ 일반 메시지 처리
            ChatMessageRequest request = objectMapper.convertValue(jsonMap, ChatMessageRequest.class);
            Long roomId = request.getRoomId();
            ChatMessageResponse saved = chatMessageService.sendMessage(roomId, user, request);
            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

            String payload = objectMapper.writeValueAsString(saved);
            for (WebSocketSession s : roomSessions.get(roomId)) {
                if (s.isOpen()) s.sendMessage(new TextMessage(payload));
            }

            log.info("📤 메시지 전송 완료 - 방: {}, 보낸사람: {}, 내용: {}", roomId, user.getId(), request.getContent());

        } catch (Exception e) {
            log.error("❌ WebSocket 메시지 처리 중 오류", e);
            try {
                session.sendMessage(new TextMessage("{\"error\":\"메시지 처리 중 오류 발생\"}"));
            } catch (Exception ignore) {}
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
                if (sessions.isEmpty()) roomSessions.remove(roomId);
            }
        }

        log.info("❎ WebSocket 연결 종료 - 세션: {}", session.getId());
    }

    private String getTokenFromQuery(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.contains("token=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring("token=".length());
                }
            }
        }
        return null;
    }
}
