package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아이디 중복 확인 요청 DTO입니다.
 * 회원가입 시 입력한 아이디가 기존 사용자와 중복되는지 확인합니다.
 */
@Getter
@NoArgsConstructor
public class UsernameCheckRequest {

    /**
     * 중복 확인할 사용자 아이디
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;
}
