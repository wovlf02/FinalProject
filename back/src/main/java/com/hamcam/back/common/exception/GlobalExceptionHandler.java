package com.hamcam.back.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.hamcam.back.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 애플리케이션 전역에서 발생하는 예외를 공통으로 처리하는 클래스
 * 각 커스텀 예외를 catch하여 에러 응답 객체로 변환하고, 클라이언트에게 적절한 상태 코드와 메시지를 반환
 *
 * 적용 대상: 모든 @RestController, @Controller 클래스
 * 반환 형식: ResponseEntity<ErrorResponse>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [400 Bad Request]
     * 잘못된 요청 (ex. 입력값 유효성 오류 등)
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(ex.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * [401 Unauthorized]
     * 인증되지 않은 사용자 요청 (ex. 토큰 없음, 만료됨 등)
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(ex.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * [403 Forbidden]
     * 접근 권한 없음 (ex. 권한 없는 리소스 접근 시)
     *
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return buildErrorResponse(ex.getErrorCode(), HttpStatus.FORBIDDEN);
    }

    /**
     * [404 Not Found]
     * 리소스를 찾을 수 없음 (ex. 존재하지 않는 사용자, 게시글 등)
     *
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * [409 Conflict]
     * 리소스 충돌 (ex. 중복 회원가입, 중복 닉네임 등)
     *
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return buildErrorResponse(ex.getErrorCode(), HttpStatus.CONFLICT);
    }

    /**
     * [500 Internal Server Error]
     * 그 외의 예외는 모두 서버 내부 에러로 처리
     *
     * @param ex 예외 내용
     * @return 예외 발생 경고 메시지
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 에러 응답객체를 생성하는 내부 메서드
     *
     * @param errorCode ErrorCode enum
     * @param status HTTP 상태 코드
     * @return ResponseEntity<ErrorResponse>
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, HttpStatus status) {
        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus()) // 예외에 해당하는 HTTP 상태 코드
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())      // 예외 코드 (ex: C400)
                        .message(errorCode.getMessage()) // 클라이언트에게 보여질 에러 메시지
                        .build());
    }
}
