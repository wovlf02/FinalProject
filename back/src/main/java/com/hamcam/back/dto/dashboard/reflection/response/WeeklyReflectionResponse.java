package com.hamcam.back.dto.dashboard.reflection.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReflectionResponse {

    /**
     * GPT가 생성한 주간 회고 텍스트
     * 예: "이번 주에는 집중률이 꾸준히 증가했습니다. 특히 금요일에는..."
     */
    private String reflectionText;
}
