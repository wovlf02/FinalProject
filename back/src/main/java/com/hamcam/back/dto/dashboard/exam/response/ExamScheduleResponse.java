package com.hamcam.back.dto.dashboard.exam.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleResponse {

    /**
     * 시험 ID
     */
    private Long id;

    /**
     * 시험명 (예: 수능, 기말고사 등)
     */
    private String title;

    /**
     * 시험 날짜 (예: 2025-06-05)
     */
    private LocalDate examDate;
}
