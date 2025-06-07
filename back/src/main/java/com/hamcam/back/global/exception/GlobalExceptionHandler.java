package com.hamcam.back.global.exception;

import com.hamcam.back.global.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /** [400 Bad Request] */
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [401 Unauthorized] */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [403 Forbidden] */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [404 Not Found] */
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [409 Conflict] */
    @ExceptionHandler(ConflictException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [CustomException] */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex) {
        return buildErrorResponse(ex.getErrorCode());
    }

    /** [500 Internal Server Error] */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace(); // 디버깅용 로그
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /** 응답 빌더 */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
