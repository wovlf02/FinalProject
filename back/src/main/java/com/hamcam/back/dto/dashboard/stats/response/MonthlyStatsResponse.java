package com.hamcam.back.dto.dashboard.stats.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatsResponse {

    /**
     * 한 달 동안의 총 공부 시간 (분 단위)
     * 예: 3890 → 64시간 50분
     */
    private int totalStudyMinutes;

    /**
     * 주차별 평균 집중률 목록 (1~5주차)
     * 예: [68, 72, 59, 75, 80]
     */
    private List<Integer> weeklyAverageFocusRates;
}
