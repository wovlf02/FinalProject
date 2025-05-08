package com.hamcam.back.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 중 발생하는 예외를 처리하기 위한 사용자 정의 예외입니다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode 기반 생성자
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 메시지 기반 간이 생성자 (ErrorCode 없이 사용할 경우)
     */
    public CustomException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
