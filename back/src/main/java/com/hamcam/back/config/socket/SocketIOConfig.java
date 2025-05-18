package com.hamcam.back.config.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 서버 Endpoint 자동 등록 설정
 * <p>
 * javax.websocket API(@ServerEndpoint)를 사용하는 경우에만 필요합니다.
 */
@Configuration
public class SocketIOConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
