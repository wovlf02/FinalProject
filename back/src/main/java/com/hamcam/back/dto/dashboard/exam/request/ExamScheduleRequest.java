package com.hamcam.back.dto.dashboard.exam.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [ExamScheduleRequest]
 * 시험 일정 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleRequest {

    @NotBlank(message = "시험명을 입력해주세요.")
    @JsonProperty("exam_name")
    private String examName;

    @NotNull(message = "시험 날짜를 입력해주세요.")
    @JsonProperty("exam_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate examDate;
}

