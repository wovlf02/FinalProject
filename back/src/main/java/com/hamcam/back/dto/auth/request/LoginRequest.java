package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [LoginRequest]
 *
 * 로그인 요청 DTO입니다.
 * 사용자 아이디와 비밀번호를 입력받아 인증 처리에 사용됩니다.
 *
 * 사용 예:
 * - POST /api/auth/login
 */
@Getter
@NoArgsConstructor
public class LoginRequest {

    /**
     * 사용자 아이디 (username)
     */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String username;

    /**
     * 사용자 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
