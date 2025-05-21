package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정 클래스 (보안 제거 버전)
 * - WebSocket 엔드포인트 등록 (/ws/chat)
 * - STOMP 메시지 전송/구독 경로 설정 (/pub, /sub)
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final StringRedisTemplate redisTemplate;

    /**
     * 클라이언트가 연결할 WebSocket 엔드포인트 등록
     * - SockJS 지원
     * - 핸드셰이크 시 CustomHandshakeHandler 사용
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * - /pub 경로로 메시지 전송
     * - /sub 경로로 구독 처리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // configureClientInboundChannel()은 제거
}
