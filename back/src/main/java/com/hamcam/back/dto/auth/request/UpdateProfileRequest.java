package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    private String studyHabit; // 선택사항
}
