package com.hamcam.back.global.exception;

import com.hamcam.back.global.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * [GlobalExceptionHandler]
 *
 * 애플리케이션 전역에서 발생하는 예외를 공통으로 처리하는 클래스입니다.
 * 각 커스텀 예외를 catch하여 에러 응답 객체(ErrorResponse)로 변환하고,
 * 클라이언트에게 적절한 HTTP 상태 코드와 메시지를 반환합니다.
 *
 * 적용 대상: 모든 @RestController, @Controller 클래스
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [400 Bad Request]
     * 잘못된 요청 예외 처리 (ex. 유효성 검증 실패, 형식 오류 등)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [401 Unauthorized]
     * 인증되지 않은 사용자 요청 (토큰 없음, 만료, 위조 등)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [403 Forbidden]
     * 인증은 되었지만 권한이 없는 요청
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [404 Not Found]
     * 요청한 리소스가 존재하지 않음
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [409 Conflict]
     * 중복된 요청 (이미 존재하는 아이디, 닉네임 등)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [CustomException]
     * 명시적인 CustomException 처리 (ErrorCode 기반)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /**
     * [500 Internal Server Error]
     * 예외로 처리되지 않은 모든 서버 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace(); // 디버깅용 로그
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * ErrorCode 기반 응답 빌더 (상태 코드 포함)
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
