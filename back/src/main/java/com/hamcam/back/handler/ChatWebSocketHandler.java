package com.hamcam.back.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        log.info("🔌 연결 완료 - 세션 ID: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Map<String, Object> json = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) json.get("type");

            if (type == null) {
                throw new IllegalArgumentException("❗ type 누락");
            }

            Long userId = extractUserIdFromSession(session);
            if (userId == null) {
                throw new IllegalArgumentException("❗ 세션에서 userId 조회 실패");
            }

            switch (type.toUpperCase()) {
                case "ENTER" -> handleEnter(session, json);
                case "READ" -> handleRead(json, userId);
                case "MESSAGE" -> handleMessage(session, json, userId);
                default -> throw new IllegalArgumentException("❗ 지원하지 않는 type: " + type);
            }

        } catch (Exception e) {
            log.error("❌ 메시지 처리 실패", e);
            try {
                session.sendMessage(new TextMessage("{\"type\":\"ERROR\",\"message\":\"처리 실패\"}"));
            } catch (Exception ignored) {}
        }
    }


    private void handleEnter(WebSocketSession session, Map<String, Object> json) {
        Long roomId = parseLong(json.get("roomId"));
        if (roomId == null) {
            log.warn("❗️ENTER 요청에서 roomId가 누락되었거나 잘못된 형식입니다.");
            return;
        }

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRoomMap.put(session.getId(), roomId);

        log.info("🚪 채팅방 입장 - 세션={}, roomId={}", session.getId(), roomId);
    }

    private void handleRead(Map<String, Object> json, Long userId) throws Exception {
        ChatReadRequest readRequest = objectMapper.convertValue(json, ChatReadRequest.class);
        int unreadCount = chatReadService.markReadAsUserId(readRequest.getRoomId(), readRequest.getMessageId(), userId);

        Map<String, Object> ack = Map.of(
                "type", "READ_ACK",
                "roomId", readRequest.getRoomId(),
                "messageId", readRequest.getMessageId(),
                "unreadCount", unreadCount
        );

        String ackPayload = objectMapper.writeValueAsString(ack);
        broadcastToRoom(readRequest.getRoomId(), ackPayload);
    }

    private void handleMessage(WebSocketSession session, Map<String, Object> json, Long userId) throws Exception {
        ChatMessageRequest request = objectMapper.convertValue(json, ChatMessageRequest.class);
        ChatMessageResponse response = chatMessageService.sendMessage(request.getRoomId(), userId, request);
        String payload = objectMapper.writeValueAsString(response);
        broadcastToRoom(request.getRoomId(), payload);
    }



    private void broadcastToRoom(Long roomId, String payload) throws Exception {
        for (WebSocketSession s : roomSessions.getOrDefault(roomId, Set.of())) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    private Long extractUserIdFromSession(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get("userId");

        if (userIdAttr instanceof Long userId) {
            return userId;
        }

        return null;
    }


    private Long parseLong(Object value) {
        try {
            return value != null ? Long.valueOf(value.toString()) : null;
        } catch (NumberFormatException e) {
            return null;
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
        log.info("❎ 연결 종료 - 세션 ID: {}", session.getId());
    }
}
