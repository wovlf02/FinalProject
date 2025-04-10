package com.hamcam.back.common.exception;

import org.springframework.http.HttpStatus;

/**
 * [BadRequestException]
 *
 * 클라이언트의 잘못된 요청(잘못된 파라미터, 형식 오류 등)에 대해
 * HTTP 400 Bad Request 응답을 반환하기 위해 사용되는 예외 클래스.
 *
 * ErrorCode 기반으로 통일하여 전역 핸들러에서 메시지와 상태코드를 일관되게 처리.
 */
public class BadRequestException extends CustomException {

    /**
     * ErrorCode 기반 생성자
     * 예시: throw new BadRequestException(ErrorCode.INVALID_EMAIL_FORMAT);
     *
     * @param errorCode 에러 코드 Enum
     */
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
