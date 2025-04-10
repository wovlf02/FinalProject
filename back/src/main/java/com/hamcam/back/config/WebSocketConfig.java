package com.hamcam.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * [WebSocketConfig]
 *
 * Spring WebSocket 메시징 설정 클래스
 * 클라이언트와 서버 간의 실시간 양방향 통신을 가능하게 하는 STOMP 기반 WebSocket 설정을 구성
 *
 * 이 설정을 통해 다음 기능이 활성화됨
 * -> 클라이언트가 연결할 수 있는 webSocket Endpoint 지정
 * STOMP 메시지 발행 경로와 구독 경로 설정
 * 내장 메시지 브로커(Simple Broker)를 통해 구독자에게 메시지 브로드캐스트
 *
 * [주요 사용 경로]
 * WebSocket 연결: /ws-stomp
 * 클라이언트 메시지 발행 /pub
 * 클라이언트 메시지 수신(구독): /sub
 *
 * ex)
 * 클라이언트가 "/pub/chat/message"로 메시지 발행
 * 서버는 "/sub/chat/room/{roomId}"를 구독 중인 클라이언트에게 메시지를 전달
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 구성 설정
     *
     * 클라이언트가 구독할 수 있는 경로 및 서버가 클라이언트로부터 메시지를 받을 때의 경로 prefix를 정의
     * @param registry MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 경로 (서버 -> 클라이언트 메시지 전송 시 사용)
        registry.enableSimpleBroker("/sub");

        // 발행 경로 prefix (클라이언트 -> 서버 메시지 전송 시 사용)
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // WebSocket 연결 시 사용할 엔드포인트
                .setAllowedOriginPatterns("*") // CORS 허용: 모든 Origin 허용
                .withSockJS(); // SockJS fallback 지원 (비 WebSocket 환경 호환)
    }
}
