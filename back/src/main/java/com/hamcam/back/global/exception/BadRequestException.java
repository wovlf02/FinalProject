package com.hamcam.back.global.exception;

/**
 * [BadRequestException]
 *
 * 클라이언트의 잘못된 요청(파라미터 누락, 유효성 검증 실패 등)에 대해
 * HTTP 400 Bad Request 응답을 반환하기 위한 커스텀 예외 클래스입니다.
 *
 * 전역 예외 핸들러(GlobalExceptionHandler)에서 처리되어
 * ErrorCode 기반으로 에러 메시지와 HTTP 상태를 일관되게 반환합니다.
 *
 * 사용 예:
 * throw new BadRequestException(ErrorCode.INVALID_INPUT);
 */
public class BadRequestException extends CustomException {

    private static final long serialVersionUID = 1L;

    /**
     * ErrorCode 기반 생성자
     * 예시: throw new BadRequestException(ErrorCode.INVALID_INPUT);
     *
     * @param errorCode 전역 에러 코드
     */
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
