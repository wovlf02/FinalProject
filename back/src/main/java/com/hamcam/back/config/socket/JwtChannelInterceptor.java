package com.hamcam.back.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * STOMP CONNECT 메시지 처리 시 Redis에서 세션 기반 인증 정보를 복원하는 인터셉터입니다.
 */
@RequiredArgsConstructor
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String userIdStr = redisTemplate.opsForValue().get("ws:session:" + sessionId);

            if (userIdStr != null) {
                Long userId = Long.parseLong(userIdStr);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                // ✅ 인증 객체를 STOMP accessor에 넣음
                accessor.setUser(authentication);

                // ✅ Spring Security 인증 컨텍스트에 등록
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context); // ✅ 필수

                System.out.println("📌 인증 객체 등록 완료: userId = " + userId);
            }
        }

        return message;
    }

}



