package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [UsernameCheckRequest]
 *
 * 아이디(Username) 중복 확인 요청 DTO입니다.
 * 회원가입 시 입력한 아이디가 기존에 사용 중인 아이디인지 확인할 때 사용됩니다.
 *
 * 사용 예:
 * - POST /api/auth/check-username
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
