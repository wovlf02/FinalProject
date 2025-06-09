package com.hamcam.back.dto.dashboard.exam.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DDayInfoResponse {

    /**
     * 시험명 (예: 중간고사)
     */
    private String title;

    /**
     * 과목명 (예: 수학)
     */
    private String subject;

    /**
     * 시험 날짜 (예: 2025-05-30 14:30)
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime examDate;

    /**
     * D-Day 계산 결과 (예: 5, 0, -2)
     */
    private Long dDay;
}
