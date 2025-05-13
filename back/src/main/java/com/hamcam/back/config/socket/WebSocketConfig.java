package com.hamcam.back.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 메시지 브로커 설정
 * 클라이언트는 /pub 으로 메시지를 전송하고, /sub 으로 수신합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 구독용 prefix: 클라이언트가 메시지를 받을 때 사용
        config.enableSimpleBroker("/sub");

        // 발신용 prefix: 클라이언트가 메시지를 보낼 때 사용
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트 (React/React Native에서 연결 시 필요)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 모바일 환경 대응용, 웹에서는 제거 가능
    }
}
