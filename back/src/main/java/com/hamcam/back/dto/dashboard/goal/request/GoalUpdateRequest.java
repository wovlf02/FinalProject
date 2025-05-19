package com.hamcam.back.dto.dashboard.goal.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUpdateRequest {

    /**
     * 변경할 하루 목표 공부 시간 (분 단위)
     * 예: 180 → 3시간
     */
    @NotNull(message = "목표 시간을 입력해주세요.")
    @Min(value = 30, message = "최소 목표 시간은 30분 이상이어야 합니다.")
    private Integer dailyGoalMinutes;
}
