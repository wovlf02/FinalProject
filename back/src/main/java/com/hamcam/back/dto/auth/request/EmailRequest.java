package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일을 단독으로 전달하는 요청 DTO입니다.
 * 이메일 중복 확인, 인증 코드 요청, 아이디 찾기 등 다양한 요청에 사용됩니다.
 */
@Getter
@NoArgsConstructor
public class EmailRequest {

    /**
     * 사용자의 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}
