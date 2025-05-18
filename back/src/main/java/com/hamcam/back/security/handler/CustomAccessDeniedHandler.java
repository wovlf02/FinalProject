package com.hamcam.back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * [CustomAccessDeniedHandler]
 *
 * 인증된 사용자가 권한이 부족한 리소스에 접근할 경우 (403 Forbidden) 실행되는 핸들러입니다.
 *
 * 예: ROLE_USER 권한이 필요한 엔드포인트에 ROLE_GUEST가 접근했을 때 발생
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 응답 상태 및 헤더 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");

        // ErrorCode 기반 에러 응답 객체 생성
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // JSON 응답 출력
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
