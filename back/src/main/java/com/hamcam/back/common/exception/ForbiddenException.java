package com.hamcam.back.common.exception;

/**
 * 접근 권한이 없는 리소스에 접근을 시도했을 때 발생하는 예외
 * ex) 타인의 채팅방/게시글 접근 시, 관리자 권한이 필요한 API 접근 시 등
 *
 * 이 예외는 전역 예외 처리기(GlobalExceptionHandler)에서 처리되어
 * 403 Forbidden 상태 코드와 함께 사용자에게 반환됨
 *
 * 사용 예시:
 * throw new ForbiddenException("해당 채팅방에 접근할 수 없습니다.");
 * throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
 */
public class ForbiddenException extends RuntimeException{

    private final ErrorCode errorCode;

    /**
     * 커스텀 메시지를 기반으로 예외 생성
     * @param message 사용자에게 전달할 메시지
     */
    public ForbiddenException(String message) {
        super(message);
        this.errorCode = ErrorCode.ACCESS_DENIED;
    }

    /**
     * ErrorCode 기반으로 예외 생성
     * @param errorCode Enum으로 정의된 에러 코드
     */
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드를 가져오는 메서드
     * @return ErrorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
