package com.hamcam.back.dto.dashboard.goal.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [GoalResetRequest]
 *
 * 사용자가 기존 설정한 학습 목표를 초기화할 때 사용하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResetRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
