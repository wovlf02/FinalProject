package com.hamcam.back.dto.dashboard.exam.response;

import lombok.*;

import java.time.LocalDate;

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
     * 시험 날짜 (예: 2025-05-30)
     */
    private LocalDate examDate;

    /**
     * D-Day 표시 문자열 (예: D-5, D-day, D+2)
     */
    private String ddayText;
}
