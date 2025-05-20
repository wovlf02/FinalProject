package com.hamcam.back.config.socket;

import com.hamcam.back.config.auth.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * WebSocket 연결 시 JWT 인증을 수행하고
 * 인증된 사용자의 정보를 Redis와 SecurityContextHolder에 등록하는 인터셉터입니다.
 */
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

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

                    // ✅ SockJS 세션 ID 추출
                    String sockJsSessionId = extractSockJsSessionId(request.getURI().getPath());

                    // ✅ Redis에 세션 정보 저장 (TTL 1시간)
                    redisTemplate.opsForValue().set("ws:session:" + sockJsSessionId, userId.toString(), Duration.ofHours(1));

                    // ✅ SecurityContext에 인증 정보 저장
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);

                    // ✅ WebSocket attributes에도 정보 저장
                    attributes.put("userId", userId);
                    attributes.put("sessionId", sockJsSessionId);

                    System.out.println("✅ WebSocket 인증 성공: userId = " + userId + ", sessionId = " + sockJsSessionId);
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
        // 연결 이후 로직 필요 시 작성
    }

    /**
     * 요청 URI에서 SockJS 세션 ID 추출
     * 예: /ws/chat/057/doufkos1/websocket → doufkos1
     */
    private String extractSockJsSessionId(String uriPath) {
        // uriPath는 보통 /ws/chat/{server-id}/{session-id}/websocket 형태
        String[] segments = uriPath.split("/");
        if (segments.length >= 4) {
            return segments[segments.length - 2]; // "doufkos1" 위치
        } else {
            // fallback (사용자 정의 UUID 또는 오류 처리)
            return "unknown-session";
        }
    }
}
