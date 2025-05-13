package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 탈퇴 요청 시 비밀번호 재확인을 위한 DTO입니다.
 * 비밀번호가 일치해야 탈퇴 처리를 진행할 수 있습니다.
 */
@Getter
@NoArgsConstructor
public class PasswordConfirmRequest {

    /**
     * 탈퇴 요청 사용자 아이디
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    /**
     * 탈퇴 요청 시 입력한 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
