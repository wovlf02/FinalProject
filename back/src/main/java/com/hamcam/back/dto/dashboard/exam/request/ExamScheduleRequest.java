package com.hamcam.back.dto.dashboard.exam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleRequest {

    /**
     * 시험명 (예: 중간고사, 수능, 모의고사 등)
     */
    @NotBlank(message = "시험명을 입력해주세요.")
    private String title;

    /**
     * 시험 날짜 (yyyy-MM-dd 형식)
     */
    @NotNull(message = "시험 날짜를 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate examDate;
}
