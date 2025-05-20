package com.hamcam.back.config.socket;

import com.hamcam.back.config.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정 클래스
 * - WebSocket 엔드포인트 등록 (/ws/chat)
 * - STOMP 메시지 전송/구독 경로 설정 (/pub, /sub)
 * - Redis 기반 세션 인증 유지 전략 적용
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;
    private final JwtChannelInterceptor jwtChannelInterceptor; // STOMP 메시지 인증 복원용
    private final StringRedisTemplate redisTemplate;

    /**
     * 클라이언트가 연결할 WebSocket 엔드포인트 등록
     * - SockJS 지원
     * - Handshake 시 쿠키 기반 accessToken 인증 수행
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtProvider, redisTemplate))
                .setHandshakeHandler(new CustomHandshakeHandler()) // Principal 생성
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

    /**
     * STOMP CONNECT 처리 시 Redis에서 sessionId 기반 인증 복원
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
