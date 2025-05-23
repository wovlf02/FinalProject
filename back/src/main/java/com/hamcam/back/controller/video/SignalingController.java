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

    /** roomId → 참여 세션 목록 */
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    /**
     * 클라이언트로부터 메시지 수신 시 처리
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String roomId = getQueryParam(session, "roomId");

        if (roomId == null) {
            log.warn("🚫 roomId가 없어 메시지를 무시함: {}", session.getId());
            return;
        }

        log.info("📨 메시지 수신 | session={}, roomId={}, message={}", session.getId(), roomId, payload);

        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, Set.of());
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    /**
     * 연결 수립 시 roomId 기반 방 등록
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getQueryParam(session, "roomId");

        if (roomId == null) {
            log.warn("❌ WebSocket 연결 거부됨: roomId 없음");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("🔌 연결 수립: session={}, roomId={}", session.getId(), roomId);
    }

    /**
     * 연결 종료 시 세션 제거 및 방 비우면 제거
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getQueryParam(session, "roomId");

        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                log.info("❎ 연결 종료: session={}, roomId={}", session.getId(), roomId);

                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                    log.info("🧹 방 제거됨: roomId={}", roomId);
                }
            }
        }
    }

    /**
     * 전송 오류 처리
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("🚨 WebSocket 오류 발생 | session={} - {}", session.getId(), exception.getMessage());
    }

    /**
     * WebSocketSession의 URI 쿼리에서 특정 파라미터 추출
     *
     * @param session WebSocket 세션
     * @param key 파라미터 키 (예: roomId, userId)
     * @return 파라미터 값 또는 null
     */
    private String getQueryParam(WebSocketSession session, String key) {
        try {
            URI uri = session.getUri();
            if (uri == null || uri.getQuery() == null) return null;

            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals(key)) {
                    return pair[1];
                }
            }
        } catch (Exception e) {
            log.warn("❗쿼리 파라미터 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
