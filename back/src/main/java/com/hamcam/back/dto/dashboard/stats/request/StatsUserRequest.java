package com.hamcam.back.dto.dashboard.stats.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 통계 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsUserRequest {

    @NotNull
    private Long userId;
}
