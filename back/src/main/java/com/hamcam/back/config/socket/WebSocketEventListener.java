package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Duration;

/**
 * WebSocket 연결/해제 이벤트 리스너
 * - Redis에 온라인 상태 저장/삭제
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final StringRedisTemplate redisTemplate;

    private static final String ONLINE_KEY_PREFIX = "online:"; // online:{userId}

    /**
     * 사용자가 WebSocket에 연결될 때 호출됨
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("userId"); // ✅ 헤더에서 userId 직접 추출

        if (userId != null) {
            redisTemplate.opsForValue().set(ONLINE_KEY_PREFIX + userId, "1", Duration.ofMinutes(30));
            log.info("✅ 온라인 등록 완료: userId = {}, sessionId = {}", userId, sessionId);
        } else {
            log.warn("⚠️ WebSocket 연결에 userId 누락: sessionId = {}", sessionId);
        }
    }

    /**
     * 사용자가 WebSocket에서 연결 종료될 때 호출됨
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.info("❌ WebSocket 연결 종료: sessionId = {}", sessionId);

        // 세션 속성에서 userId 추출
        Object userIdObj = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("userId")
                : null;

        if (userIdObj != null) {
            String userId = userIdObj.toString();
            String key = ONLINE_KEY_PREFIX + userId;
            if (Boolean.TRUE.equals(redisTemplate.delete(key))) {
                log.info("🧹 온라인 상태 제거 완료: {}", key);
            } else {
                log.warn("⚠️ 온라인 상태 키가 없거나 삭제 실패: {}", key);
            }
        } else {
            log.warn("⚠️ 연결 종료된 세션에 userId 없음: sessionId = {}", sessionId);
        }
    }
}
