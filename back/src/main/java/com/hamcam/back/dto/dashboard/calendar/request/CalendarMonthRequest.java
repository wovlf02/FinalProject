package com.hamcam.back.dto.dashboard.calendar.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

/**
 * [CalendarMonthRequest]
 *
 * 월별 캘린더 이벤트 요청 DTO
 * - 사용자 ID와 조회할 월 정보를 포함함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarMonthRequest {

    @NotNull(message = "조회할 월(month)은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth month;
}
