package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 */
@Getter
@Setter
public class LoginRequest {

    /**
     * 아이디
     * 3~50자
     * 필수 입력
     */
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하로 입력해주세요.")
    private String username;

    /**
     * 비밀번호
     * 8~100자
     * 필수 입력
     */
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 255자 이하로 입력해주세요.")
    private String password;
}
