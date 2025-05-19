package com.hamcam.back.global.exception;

import lombok.Getter;

/**
 * [CustomException]
 *
 * 비즈니스 로직 중 발생하는 예외를 처리하기 위한 최상위 사용자 정의 예외 클래스입니다.
 * 모든 도메인 예외는 이 클래스를 상속하여 사용하며, ErrorCode와 메시지를 통일성 있게 제공합니다.
 *
 * [예시]
 * throw new CustomException(ErrorCode.USER_NOT_FOUND);
 * throw new CustomException("예상치 못한 오류가 발생했습니다.");
 */
@Getter
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 에러 코드 기반 예외 구분
     */
    private final ErrorCode errorCode;

    /**
     * ErrorCode 기반 생성자
     * @param errorCode 정의된 에러 코드 (HTTP 상태 포함)
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 메시지 기반 간이 생성자 (에러 코드 없이 사용 시 INTERNAL_SERVER_ERROR 처리)
     * @param message 예외 메시지
     */
    public CustomException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
