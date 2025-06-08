package com.hamcam.back.dto.dashboard.reflection.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
        import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [RangeReflectionRequest]
 *
 * 기간 기반 회고 요청 DTO
 * - 사용자 ID, 시작일, 종료일 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangeReflectionRequest {

    @NotNull(message = "시작일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
