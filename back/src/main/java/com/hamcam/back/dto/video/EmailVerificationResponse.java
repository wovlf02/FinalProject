package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증번호 전송 결과를 반환하는 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class EmailVerificationResponse {

    /**
     * 이메일 인증번호 전송 결과 메시지
     */
    private String message;
}
