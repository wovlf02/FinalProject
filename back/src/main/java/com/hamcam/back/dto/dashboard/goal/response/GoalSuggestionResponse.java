package com.hamcam.back.dto.dashboard.goal.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalSuggestionResponse {

    /**
     * GPT가 생성한 메시지
     * 예: "최근 집중률이 낮아 하루 2시간을 추천합니다."
     */
    private String message;

    /**
     * GPT가 제안한 목표 공부 시간 (분 단위)
     * 예: 150 → 2시간 30분
     */
    private Integer suggestedDailyGoalMinutes;
}
