package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUniversityRequest {
    @NotNull(message = "대학교 ID는 필수 입력값입니다.")
    private Long universityId;
}