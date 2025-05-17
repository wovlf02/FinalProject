package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUsernameRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String username;
}