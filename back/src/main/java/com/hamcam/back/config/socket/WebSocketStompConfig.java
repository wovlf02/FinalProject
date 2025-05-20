package com.hamcam.back.config.socket;

import com.hamcam.back.config.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정 클래스
 * - /ws/chat 엔드포인트 등록
 * - STOMP 메시지 브로커 설정
 * - 핸드셰이크 시 JWT 기반 인증 처리
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;

    /**
     * 클라이언트가 WebSocket 연결을 시도할 엔드포인트 등록
     * - SockJS 지원
     * - JWT 인증을 위한 HandshakeInterceptor 추가
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // 프론트엔드 도메인 허용
                .addInterceptors(new JwtHandshakeInterceptor(jwtProvider)) // JWT 인증 처리
                .withSockJS(); // SockJS fallback 지원
    }

    /**
     * STOMP 메시지 브로커 구성
     * - 클라이언트 전송 경로: /app/...
     * - 구독 브로커 경로: /topic, /queue
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 구독용 경로
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 송신 prefix
    }
}
