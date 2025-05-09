package com.hamcam.back.global.exception;

/**
 * [ConflictException]
 *
 * 클라이언트의 요청이 서버 상태와 충돌할 때 발생하는 예외
 * 예를 들어, 중복된 이메일, 이미 존재하는 리소스 요청 시 사용
 *
 * [사용 예시]
 * throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
 *
 * [HTTP 응답 코드]
 * 409 Conflict
 */
public class ConflictException extends CustomException {

    /**
     * [생성자]
     *
     * @param errorCode ErrorCode Enum (예: EMAIL_ALREADY_EXISTS 등)
     */
    public ConflictException(ErrorCode errorCode) {
        super(errorCode); // 부모 클래스에서 errorCode 기반으로 메시지/상태 설정
    }
}
