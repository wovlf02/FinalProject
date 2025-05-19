package com.hamcam.back.global.exception;

import lombok.Getter;

/**
 * [ForbiddenException]
 *
 * 인증은 되었으나 접근 권한이 없는 리소스에 접근하려 할 때 발생하는 예외입니다.
 * 예: 타인의 리소스(채팅방, 게시글 등)에 접근하거나, 관리 권한이 없는 API 호출 등
 *
 * 전역 예외 처리기(GlobalExceptionHandler)에서 이 예외를 감지하여
 * HTTP 403 Forbidden 응답을 반환합니다.
 *
 * [사용 예시]
 * throw new ForbiddenException("해당 채팅방에 접근할 수 없습니다.");
 * throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    /**
     * 사용자 정의 메시지를 포함한 예외 생성자
     * (ErrorCode는 기본적으로 ACCESS_DENIED로 설정)
     *
     * @param message 사용자에게 보여줄 에러 메시지
     */
    public ForbiddenException(String message) {
        super(message);
        this.errorCode = ErrorCode.ACCESS_DENIED;
    }

    /**
     * ErrorCode 기반 예외 생성자
     *
     * @param errorCode ErrorCode Enum 값
     */
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
