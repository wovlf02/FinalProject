package com.hamcam.back.dto.dashboard.stats.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalStatsResponse {

    /**
     * 전체 누적 공부 시간 (분 단위)
     */
    private int totalStudyMinutes;

    /**
     * 평균 집중률 (0~100%)
     */
    private int averageFocusRate;

    /**
     * 평균 정확도 (0~100%)
     */
    private int averageAccuracy;
}
