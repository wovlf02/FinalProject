package com.hamcam.back.config.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 연결/해제 이벤트 리스너
 * - 연결/종료 로그
 * - 유저 세션 추적 또는 연결자 수 관리 용도로 사용 가능
 */
@Slf4j
@Component
public class WebSocketEventListener {

    /**
     * 사용자가 WebSocket에 연결될 때 호출됨
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.info("🔌 WebSocket 연결됨: sessionId = {}", sessionId);
    }

    /**
     * 사용자가 WebSocket에서 연결 종료 시 호출됨
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.info("❌ WebSocket 연결 종료: sessionId = {}", sessionId);

        // 세션 기반으로 userId 추적하고 싶을 경우 attributes 사용
        Object userId = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("userId")
                : null;

        if (userId != null) {
            log.info("⛔ 종료된 사용자 ID: {}", userId);
            // TODO: 사용자 접속 상태 업데이트 등 처리
        }
    }
}
