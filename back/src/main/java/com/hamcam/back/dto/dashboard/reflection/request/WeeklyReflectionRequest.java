package com.hamcam.back.dto.dashboard.reflection.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [WeeklyReflectionRequest]
 *
 * 주간 회고 생성 요청 DTO
 * - 사용자 ID와 회고 대상 주간의 시작일/종료일 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReflectionRequest {

    @NotNull(message = "시작일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
