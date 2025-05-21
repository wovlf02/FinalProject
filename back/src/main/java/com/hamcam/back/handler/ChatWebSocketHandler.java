package com.hamcam.back.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hamcam.back.dto.community.chat.request.ChatMessageRequest;
import com.hamcam.back.dto.community.chat.request.ChatReadRequest;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatMessageService;
import com.hamcam.back.service.community.chat.ChatReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [ChatWebSocketHandler]
 *
 * JWT 제거된 WebSocket 핸들러.
 * - 클라이언트가 userId를 메시지에 직접 포함하도록 구조 변경.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final ChatReadService chatReadService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("🔌 WebSocket 연결됨 - 세션 ID: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) jsonMap.get("type");

            if (type == null || !jsonMap.containsKey("userId")) {
                throw new IllegalArgumentException("userId가 누락되었습니다.");
            }

            Long userId = Long.valueOf(jsonMap.get("userId").toString());

            switch (type.toUpperCase()) {
                case "ENTER" -> {
                    Long roomId = Long.valueOf(jsonMap.get("roomId").toString());
                    roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
                    sessionRoomMap.put(session.getId(), roomId);
                    chatReadService.updateLastReadMessage(roomId, userId);
                    log.info("🚪 입장 - 사용자 {} / 방 {}", userId, roomId);
                }

                case "READ" -> {
                    ChatReadRequest readRequest = objectMapper.convertValue(jsonMap, ChatReadRequest.class);
                    int unreadCount = chatReadService.markReadAsUserId(
                            readRequest.getRoomId(), readRequest.getMessageId(), userId
                    );

                    Map<String, Object> ack = new HashMap<>();
                    ack.put("type", "READ_ACK");
                    ack.put("roomId", readRequest.getRoomId());
                    ack.put("messageId", readRequest.getMessageId());
                    ack.put("unreadCount", unreadCount);

                    String ackPayload = objectMapper.writeValueAsString(ack);
                    for (WebSocketSession s : roomSessions.getOrDefault(readRequest.getRoomId(), Set.of())) {
                        if (s.isOpen()) s.sendMessage(new TextMessage(ackPayload));
                    }
                }

                default -> {
                    ChatMessageRequest request = objectMapper.convertValue(jsonMap, ChatMessageRequest.class);
                    ChatMessageResponse response = chatMessageService.sendMessage(request.getRoomId(), userId, request);

                    String payload = objectMapper.writeValueAsString(response);
                    for (WebSocketSession s : roomSessions.getOrDefault(request.getRoomId(), Set.of())) {
                        if (s.isOpen()) s.sendMessage(new TextMessage(payload));
                    }
                }
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
}
