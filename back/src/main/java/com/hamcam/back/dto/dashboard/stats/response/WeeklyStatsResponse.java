package com.hamcam.back.dto.dashboard.stats.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyStatsResponse {

    /**
     * 일자별 공부 통계 리스트 (최근 7일)
     */
    private List<DailyStat> dailyStats;

    /**
     * 과목별 주간 성장률 리스트
     */
    private List<GrowthResponse> growthList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStat {

        /**
         * 날짜 (yyyy-MM-dd)
         */
        private LocalDate date;

        /**
         * 해당 날짜의 총 공부 시간 (분 단위)
         */
        private int studyMinutes;

        /**
         * 해당 날짜의 경고 횟수 (집중 방해 횟수 등)
         */
        private int warningCount;
    }
}
