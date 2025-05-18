package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공부 습관 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateStudyHabitRequest {

    @NotBlank(message = "공부 습관은 필수 입력 값입니다.")
    private String studyHabit;
}
