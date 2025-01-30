package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 인증 요청 DTO -> 회원가입 및 로그인
 */
public class AuthRequest {

    /**
     * 회원가입 요청 DTO
     */
    @Getter
    @Setter
    public static class RegisterRequest {

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Size(min = 4, max = 50, message = "아이디는 4 ~ 50자로 입력해야 합니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Size(max = 100, message = "이름은 최대 100자까지 입력할 수 있습니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        @Size(max = 15, message = "전화번호는 최대 15자까지 입력할 수 있습니다.")
        private String phone;

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Size(message = "유효한 이메일 주소를 입력해야 합니다.")
        private String email;
    }

    /**
     * 로그인 요청 DTO
     */
    @Getter
    @Setter
    public static class LoginRequest {

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;
    }
}
