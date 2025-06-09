package com.hamcam.back.dto.dashboard.exam.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    /**
     * 시험명 (예: 중간고사, 모의고사 등)
     */
    @NotBlank(message = "시험 제목은 필수입니다.")
    @Size(min = 2, max = 100, message = "시험 제목은 2자 이상 100자 이하여야 합니다.")
    @JsonProperty("title")
    private String title;

    /**
     * 과목명 (예: 수학, 영어 등)
     */
    @Size(max = 50, message = "과목명은 50자 이하여야 합니다.")
    @JsonProperty("subject")
    private String subject;

    /**
     * 시험 날짜 (예: 2025-05-30)
     */
    @NotNull(message = "시험 날짜는 필수입니다.")
    @JsonProperty("exam_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate examDate;

    /**
     * 시험 설명 (선택)
     */
    @Size(max = 500, message = "시험 설명은 500자 이하여야 합니다.")
    private String description;

    /**
     * 시험 장소 (선택)
     */
    @Size(max = 200, message = "시험 장소는 200자 이하여야 합니다.")
    private String location;
}

