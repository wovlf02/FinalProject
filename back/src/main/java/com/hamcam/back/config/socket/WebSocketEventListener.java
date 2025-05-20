package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 연결/해제 이벤트 리스너
 * - Redis 세션 클린업
 * - 유저 세션 추적 또는 연결자 수 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final StringRedisTemplate redisTemplate;

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
     * 사용자가 WebSocket에서 연결 종료될 때 호출됨
     * - Redis에 저장된 sessionId → userId 매핑 정보 제거
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.info("❌ WebSocket 연결 종료: sessionId = {}", sessionId);

        // Redis 세션 정보 삭제
        String redisKey = "ws:session:" + sessionId;
        Boolean deleted = redisTemplate.delete(redisKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("🧹 Redis 세션 삭제 완료: key = {}", redisKey);
        } else {
            log.warn("⚠️ Redis 세션 삭제 실패 또는 없음: key = {}", redisKey);
        }

        // 선택: 세션에서 사용자 ID 확인 (로그 추적 용도)
        Object userId = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("userId")
                : null;

        if (userId != null) {
            log.info("⛔ 종료된 사용자 ID: {}", userId);
        }
    }
}
