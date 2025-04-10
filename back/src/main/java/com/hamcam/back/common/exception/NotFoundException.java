package com.hamcam.back.common.exception;

import lombok.Getter;

/**
 * [NotFoundException]
 *
 * 요청한 리소스를 찾을 수 없을 때 발생하는 예외 클래스입니다.
 * ErrorCode를 통해 어떤 리소스를 찾을 수 없는지 명확하게 구분할 수 있습니다.
 *
 * 사용 예:
 * throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
 * throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
 */
@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode 기반 생성자
     * USER_NOT_FOUND, POST_NOT_FOUND 등 구체적인 에러코드를 전달
     *
     * @param errorCode ErrorCode (예: ErrorCode.USER_NOT_FOUND)
     */
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode + 커스텀 메시지 기반 생성자
     * 기본 메시지 외에 상세 메시지를 로그용으로 전달할 수 있음
     *
     * @param errorCode ErrorCode
     * @param detailMessage 로그 또는 디버깅용 상세 메시지
     */
    public NotFoundException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
