// StudySessionResponse.java
package com.hamcam.back.dto.study.personal.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionResponse {
    private Long sessionId;
    private String unitName;
    private int durationMinutes;
    private String studyType;
    private String createdAt;
}
