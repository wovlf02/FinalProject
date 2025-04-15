package com.studymate.back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증 요청 DTO
 */
@Getter
@Setter
public class EmailVerificationRequest {

    /**
     * 이메일
     * 필수 입력
     */
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    /**
     * 인증번호
     * 필수입력
     */
    @NotBlank(message = "인증번호를 입력해주세요")
    private String code;
}
