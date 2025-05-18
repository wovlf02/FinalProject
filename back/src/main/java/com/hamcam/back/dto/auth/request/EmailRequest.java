package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * [EmailRequest]
 *
 * 이메일 주소를 전달하는 요청 DTO입니다.
 * 이메일 인증 요청, 중복 확인, 계정 찾기 등 다양한 인증 플로우에 활용됩니다.
 *
 * 사용 예:
 * - POST /auth/email/verify
 * - POST /auth/email/duplicate-check
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    /**
     * 사용자 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}
