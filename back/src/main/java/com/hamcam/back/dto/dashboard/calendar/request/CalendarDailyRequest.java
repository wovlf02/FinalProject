package com.hamcam.back.dto.dashboard.calendar.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [CalendarDailyRequest]
 *
 * 특정 날짜의 캘린더 이벤트(Todo, 시험, 공부 시간)를 조회하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDailyRequest {

    @NotNull(message = "날짜는 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
}
