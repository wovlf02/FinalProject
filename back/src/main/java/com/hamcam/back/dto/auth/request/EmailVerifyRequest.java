package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [EmailVerifyRequest]
 *
 * 이메일 인증 코드 검증 요청 DTO입니다.
 * 사용자가 입력한 인증 코드가 Redis에 저장된 값과 일치하는지 확인할 때 사용됩니다.
 *
 * 사용 예:
 * - POST /auth/email/verify
 */
@Getter
@NoArgsConstructor
public class EmailVerifyRequest {

    /**
     * 인증 대상 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    /**
     * 사용자가 입력한 인증 코드
     */
    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;
}
