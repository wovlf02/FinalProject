package com.hamcam.back.controller.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [SignalingController]
 * WebRTC 통신을 위한 signaling WebSocket 핸들러 (roomId 기반 브로드캐스트 처리)
 */
@Slf4j
@Component
public class SignalingController extends TextWebSocketHandler {

    /** roomId -> 참여 세션 목록 */
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessionIdMap = new ConcurrentHashMap<>(); // sessionId → session
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 클라이언트 메시지 수신 시 → 대상에게 전송 또는 전체 브로드캐스트
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        String roomId = getQueryParam(session, "roomId");

        if (roomId == null) {
            log.warn("🚫 roomId 누락 - 수신 메시지 무시됨: session={}", session.getId());
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            String senderId = root.path("senderId").asText();
            String targetId = root.has("targetId") ? root.path("targetId").asText(null) : null;

            log.info("📨 signaling 수신 | roomId={}, senderId={}, targetId={}, payload={}", roomId, senderId, targetId, payload);

            if (targetId != null && sessionIdMap.containsKey(targetId)) {
                WebSocketSession targetSession = sessionIdMap.get(targetId);
                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(payload));
                }
            } else {
                Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, Collections.emptySet());
                for (WebSocketSession s : sessions) {
                    if (s.isOpen() && !s.equals(session)) {
                        s.sendMessage(new TextMessage(payload));
                    }
                }
            }

        } catch (Exception e) {
            log.error("❌ 메시지 파싱/전송 실패 | session={}, error={}", session.getId(), e.getMessage());
        }
    }

    /**
     * 연결 수립 시 방 세션에 등록
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getQueryParam(session, "roomId");

        if (roomId == null) {
            log.warn("❌ WebSocket 연결 거부: roomId 없음 | session={}", session.getId());
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionIdMap.put(session.getId(), session);

        log.info("🔌 WebSocket 연결됨 | roomId={}, session={}", roomId, session.getId());
    }

    /**
     * 연결 종료 시 세션 제거 + 방 비우면 삭제
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = getQueryParam(session, "roomId");

        if (roomId != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                log.info("❎ 연결 종료 | roomId={}, session={}, status={}", roomId, session.getId(), status);

                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                    log.info("🧹 방 제거됨 | roomId={}", roomId);
                }
            }
        }

        sessionIdMap.remove(session.getId());
    }

    /**
     * 연결 중 에러 처리
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("🚨 WebSocket 오류 | session={}, error={}", session.getId(), exception.getMessage());
    }

    /**
     * 세션의 URI 쿼리 파라미터에서 특정 값 추출
     */
    private String getQueryParam(WebSocketSession session, String key) {
        try {
            URI uri = session.getUri();
            if (uri == null || uri.getQuery() == null) return null;

            for (String param : uri.getQuery().split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals(key)) {
                    return URLDecoder.decode(pair[1], "UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.warn("❗ URL 디코딩 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("❗ URI 파싱 실패: {}", e.getMessage());
        }
        return null;
    }
}
