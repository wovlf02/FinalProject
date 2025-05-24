package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [UserRequest]
 *
 * 사용자 관련 요청을 위한 공통 DTO입니다.
 * 사용자 ID를 포함해 닉네임, 이메일, 아이디(username) 등의 변경 요청에도 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    private String nickname;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String username;
}
