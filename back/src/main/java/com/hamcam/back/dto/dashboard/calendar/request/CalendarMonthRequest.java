package com.hamcam.back.dto.dashboard.calendar.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
public class CalendarMonthRequest {

    /**
     * 조회할 월 (예: 2025-05)
     */
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth month;

    public CalendarMonthRequest(YearMonth month) {
        this.month = month;
    }
}