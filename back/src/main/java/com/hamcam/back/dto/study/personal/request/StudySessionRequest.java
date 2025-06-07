// StudySessionRequest.java
package com.hamcam.back.dto.study.personal.request;

import lombok.*;

@Getter
@Setter
public class StudySessionRequest {
    private int durationMinutes;
    private String subject;        // optional
    private String studyType;      // optional: enum으로 사용하려면 파싱 필요
    private String unitName;       // optional
}
