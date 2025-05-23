package com.hamcam.back.dto.dashboard.goal.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 목표 추천 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUserRequest {

    @NotNull
    private Long userId;
}
