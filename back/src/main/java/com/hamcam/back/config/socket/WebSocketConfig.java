package com.hamcam.back.config.socket;

import com.hamcam.back.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 설정 클래스 (STOMP 미사용)
 * <p>
 * 클라이언트는 ws://서버주소/ws/chat 로 연결되며,
 * SockJS를 통해 fallback 및 브라우저 호환성을 제공합니다.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS fallback 지원
    }
}
