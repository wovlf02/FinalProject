package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정 클래스
 * - /ws        : 전역 소켓 (온라인 상태, 알림 등)
 * - /ws/team   : 팀방 전용 소켓 (실시간 문제풀이, 캠 공유 등)
 * - /ws/chat   : 채팅 전용 소켓
 * - /pub       : 메시지 발행 경로
 * - /sub       : 메시지 수신(구독) 경로
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandshakeInterceptor stompHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ WebSocket Endpoint 공통 설정
        String[] allowedOrigins = {
                "http://localhost:8080",          // 로컬 개발용
                "https://*.ngrok-free.app"        // ngrok 환경 허용
        };

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // ✅ credentials: true를 쓰려면 * 대신 정확한 origin 쓰거나 "*" 사용 가능 (Spring 5.3+)
                .addInterceptors(stompHandshakeInterceptor)
                .withSockJS()
                .setSessionCookieNeeded(true);

        registry.addEndpoint("/ws/team")
                .setAllowedOriginPatterns("*")
                .addInterceptors(stompHandshakeInterceptor)
                .withSockJS()
                .setSessionCookieNeeded(true);

        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(stompHandshakeInterceptor)
                .withSockJS()
                .setSessionCookieNeeded(true);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
