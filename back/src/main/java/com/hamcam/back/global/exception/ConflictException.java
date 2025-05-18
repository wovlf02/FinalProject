package com.hamcam.back.global.exception;

/**
 * [ConflictException]
 *
 * 클라이언트의 요청이 서버 상태와 충돌할 때 발생하는 예외.
 * 예: 이미 존재하는 이메일, 아이디, 닉네임 등 중복 자원 요청.
 *
 * 사용 예시:
 * throw new ConflictException(ErrorCode.DUPLICATE_EMAIL);
 *
 * HTTP 상태 코드: 409 Conflict
 */
public class ConflictException extends CustomException {

    /**
     * 생성자 - ErrorCode를 기반으로 ConflictException 생성
     *
     * @param errorCode 중복 자원 관련 에러 코드
     */
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
