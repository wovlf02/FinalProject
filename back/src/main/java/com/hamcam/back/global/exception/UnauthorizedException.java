package com.hamcam.back.global.exception;

import lombok.Getter;

/**
 * [UnauthorizedException]
 *
 * 인증되지 않은 사용자의 요청이 발생했을 때 던지는 예외 클래스입니다.
 * 예: 로그인하지 않은 사용자가 인증이 필요한 요청을 보냈을 경우
 *
 * 사용 예시:
 * throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
 * throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "토큰 형식이 잘못되었습니다.");
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 기본 생성자 - ErrorCode만 전달
     *
     * @param errorCode ErrorCode Enum (예: UNAUTHORIZED, INVALID_TOKEN 등)
     */
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 상세 메시지를 포함한 생성자
     * ErrorCode와 함께 커스텀 메시지를 로그용으로 전달할 수 있음
     *
     * @param errorCode ErrorCode Enum
     * @param detailMessage 로그 또는 디버깅용 상세 메시지
     */
    public UnauthorizedException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
