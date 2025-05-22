package com.hamcam.back.dto.dashboard.exam.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [ExamScheduleRequest]
 *
 * 시험 일정 생성 요청 DTO
 * - 사용자 ID와 시험 제목/날짜 정보를 포함함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 시험명 (예: 중간고사, 수능, 모의고사 등)
     */
    @NotBlank(message = "시험명을 입력해주세요.")
    private String title;

    /**
     * 시험 날짜 (예: 2025-06-10)
     */
    @NotNull(message = "시험 날짜를 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate examDate;
}
