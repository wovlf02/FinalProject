package com.hamcam.back.config.socket;

import com.hamcam.back.config.auth.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            Cookie[] cookies = httpRequest.getCookies();
            if (cookies == null) {
                System.out.println("❌ WebSocket 연결 실패: 쿠키 없음");
                return false;
            }

            String token = null;
            for (Cookie cookie : cookies) {
                if (JwtProvider.ACCESS_COOKIE.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

            if (token == null) {
                System.out.println("❌ WebSocket 연결 실패: accessToken 쿠키 없음");
                return false;
            }

            try {
                if (jwtProvider.validateTokenWithoutRedis(token)) {
                    Long userId = jwtProvider.getUserIdFromToken(token);
                    attributes.put("userId", userId);
                    System.out.println("✅ WebSocket 연결 성공: userId=" + userId);
                    return true;
                } else {
                    System.out.println("❌ WebSocket 연결 실패: accessToken 검증 실패");
                }
            } catch (Exception e) {
                System.out.println("❌ WebSocket 연결 실패: " + e.getMessage());
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 필요 시 연결 후 처리 로직 작성 가능 (예: 로깅)
    }
}
