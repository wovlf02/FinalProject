package com.hamcam.back.controller.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [SignalingController]
 *
 * WebRTC 통신을 위한 Signaling 서버 역할을 수행하는 WebSocket 핸들러
 * - 모든 세션에 시그널링 메시지를 브로드캐스팅
 * - Peer 간 offer, answer, candidate 메시지를 중계
 */
@Slf4j
@Component
public class SignalingController extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 클라이언트로부터 메시지를 수신했을 때 동작
     * - 모든 연결된 사용자에게 메시지를 브로드캐스트 (단순한 방 구조)
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("📨 수신 메시지 from {}: {}", session.getId(), payload);

        // 모든 세션에 메시지 전송 (자기 자신 포함)
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    /**
     * 새로운 클라이언트가 WebSocket에 연결되었을 때 호출됨
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("🔌 연결됨: {}", session.getId());
    }

    /**
     * 클라이언트 연결 종료 시 호출됨
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("❎ 연결 종료: {} ({})", session.getId(), status.getReason());
    }

    /**
     * 예외 발생 시 로깅 처리
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("🚨 WebSocket 오류 (세션: {}): {}", session.getId(), exception.getMessage());
    }
}
