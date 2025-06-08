package com.hamcam.back.dto.dashboard.exam.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleResponse {

    /**
     * 시험 ID (필요 시 사용)
     */
    private Long id;

    /**
     * 시험명 (예: 수능, 기말고사 등)
     */
    private String examName; // ✅ title → examName으로 명확히 통일

    /**
     * 시험 날짜 (예: 2025-06-05)
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate examDate;
}
