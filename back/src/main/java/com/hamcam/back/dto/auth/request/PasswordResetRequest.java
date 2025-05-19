package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PasswordResetRequest]
 *
 * 비밀번호 재설정 본인 확인 요청 DTO입니다.
 * 입력한 아이디와 이메일이 회원 정보와 일치하는지 확인 후
 * 이메일로 인증 코드를 발송하기 위한 요청에 사용됩니다.
 *
 * 사용 예:
 * - POST /api/auth/password/reset/request
 */
@Getter
@NoArgsConstructor
public class PasswordResetRequest {

    /**
     * 비밀번호를 재설정할 사용자 아이디
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    /**
     * 해당 아이디와 연결된 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}
