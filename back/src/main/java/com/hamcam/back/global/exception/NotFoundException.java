package com.hamcam.back.global.exception;

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
     * 예: ErrorCode.USER_NOT_FOUND, ErrorCode.POST_NOT_FOUND 등
     *
     * @param errorCode ErrorCode Enum 객체
     */
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 사용자 지정 메시지를 함께 전달
     * 로그 등 디버깅 목적의 상세 메시지를 추가로 포함시킬 수 있습니다.
     *
     * @param errorCode ErrorCode Enum 객체
     * @param detailMessage 상세 설명 메시지 (ex: "ID 3번 유저가 존재하지 않음")
     */
    public NotFoundException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
