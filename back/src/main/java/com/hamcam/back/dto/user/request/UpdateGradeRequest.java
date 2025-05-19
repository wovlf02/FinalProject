package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학년 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateGradeRequest {

    @NotNull(message = "학년은 필수 입력 값입니다.")
    @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
    @Max(value = 3, message = "학년은 3 이하여야 합니다.")
    private Integer grade;
}
