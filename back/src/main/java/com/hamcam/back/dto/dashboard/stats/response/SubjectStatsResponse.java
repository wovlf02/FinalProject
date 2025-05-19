package com.hamcam.back.dto.dashboard.stats.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectStatsResponse {

    /**
     * 과목명 (예: 수학, 영어, 과학 등)
     */
    private String subjectName;

    /**
     * 총 집중 시간 (분 단위)
     */
    private int totalFocusMinutes;

    /**
     * 평균 정확도 (0 ~ 100%)
     */
    private int averageAccuracy;

    /**
     * 평균 정답률 (0 ~ 100%)
     */
    private int averageCorrectRate;
}
