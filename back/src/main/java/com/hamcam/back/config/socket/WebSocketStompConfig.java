package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * STOMP 기반 WebSocket 설정 클래스 (세션 기반 인증 적용)
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final StringRedisTemplate redisTemplate;

    /**
     * 클라이언트가 연결할 WebSocket 엔드포인트 등록
     * - SockJS 지원
     * - 세션 정보 전달을 위한 HttpSessionHandshakeInterceptor 추가
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor()) // ✅ 세션 정보를 WebSocket 세션으로 전달
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * - /pub: 메시지 전송용
     * - /sub: 구독용
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
