package com.hamcam.back.dto.dashboard.exam.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 사용자 기반 시험 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamUserRequest {

    @NotNull
    private Long userId;
}
