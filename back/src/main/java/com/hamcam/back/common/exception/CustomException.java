package com.hamcam.back.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 모든 커스텀 예외 클래스의 부모 클래스임
 * HttpStatus와 message를 함께 설정할 수 있도록 함
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // 메시지는 ErrorCode에서 가져옴
        this.errorCode = errorCode;
    }
}
