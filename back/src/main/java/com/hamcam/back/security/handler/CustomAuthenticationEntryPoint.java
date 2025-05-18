package com.hamcam.back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * [CustomAuthenticationEntryPoint]
 *
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 경우 호출되는 EntryPoint
 * (예: JWT 누락, 만료 또는 미인증 상태에서 요청 시)
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 상태 코드 및 Content-Type 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        // ErrorCode 기반 에러 응답 구성
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // JSON 응답 전송
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
