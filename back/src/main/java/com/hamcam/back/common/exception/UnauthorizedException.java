package com.hamcam.back.common.exception;

import lombok.Getter;

/**
 * [UnauthorizedException]
 *
 * 인증되지 않은 사용자의 요청이 발생했을 때 던지는 예외 클래스입니다.
 * 예: 로그인하지 않은 사용자가 인증이 필요한 요청을 보냈을 경우
 *
 * [사용 예시]
 * throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
 * throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED, "토큰이 만료되었습니다.");
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 기본 생성자 - ErrorCode만 전달
     *
     * @param errorCode ErrorCode (예: UNAUTHORIZED, TOKEN_EXPIRED 등)
     */
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 상세 메시지 포함 생성자
     *
     * @param errorCode ErrorCode
     * @param detailMessage 디버깅 또는 로그용 상세 메시지
     */
    public UnauthorizedException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
