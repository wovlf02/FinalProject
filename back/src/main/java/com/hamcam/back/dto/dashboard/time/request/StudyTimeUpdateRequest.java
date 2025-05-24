package com.hamcam.back.dto.dashboard.time.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyTimeUpdateRequest {
    private int weeklyGoalMinutes;
    private int todayGoalMinutes;
    private int todayStudyMinutes;
}
