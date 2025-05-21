package com.hamcam.back.controller.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [SignalingController]
 *
 * WebRTC 통신을 위한 signaling WebSocket 핸들러 (방 기반 확장)
 */
@Slf4j
@Component
public class SignalingController extends TextWebSocketHandler {

    // roomId → [세션 목록]
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    /**
     * 클라이언트로부터 메시지 수신 시
     * - 같은 roomId에 속한 세션에만 메시지를 브로드캐스트
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String roomId = getRoomIdFromSession(session);

        log.info("📨 메시지 수신 | session={}, roomId={}, message={}", session.getId(), roomId, payload);

        if (roomId != null) {
            for (WebSocketSession s : roomSessions.getOrDefault(roomId, Set.of())) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    /**
     * 연결 시 roomId 파라미터로 방 등록
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomIdFromSession(session);
        if (roomId == null) {
            log.warn("❌ 연결 거부됨: roomId 없음");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("🔌 연결됨: session={}, roomId={}", session.getId(), roomId);
    }

    /**
     * 연결 종료 시 방에서 제거
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomIdFromSession(session);
        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                log.info("❎ 연결 종료: session={}, roomId={}", session.getId(), roomId);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }
    }

    /**
     * 에러 핸들링
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("🚨 WebSocket 오류 | session={}: {}", session.getId(), exception.getMessage());
    }

    /**
     * URI 쿼리에서 roomId 파라미터 추출
     * ex: ws://localhost:8080/ws/signal?roomId=abc123&userId=1
     */
    private String getRoomIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null) return null;

            String query = uri.getQuery(); // roomId=abc123&userId=1
            if (query == null) return null;

            for (String param : query.split("&")) {
                String[] parts = param.split("=");
                if (parts.length == 2 && parts[0].equals("roomId")) {
                    return parts[1];
                }
            }
        } catch (Exception e) {
            log.warn("roomId 파싱 실패: {}", e.getMessage());
        }
        return null;
    }
}
