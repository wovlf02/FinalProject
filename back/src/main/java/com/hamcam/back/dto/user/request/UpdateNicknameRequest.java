package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;
}
