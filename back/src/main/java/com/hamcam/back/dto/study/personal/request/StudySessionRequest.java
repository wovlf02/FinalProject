// StudySessionRequest.java
package com.hamcam.back.dto.study.personal.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionRequest {
    private String unitName;        // 예: "개인 공부"
    private int durationMinutes;    // 공부한 시간 (분 단위)
    private String studyType;       // PERSONAL, 추후 TEAM도 가능성 있음
}
