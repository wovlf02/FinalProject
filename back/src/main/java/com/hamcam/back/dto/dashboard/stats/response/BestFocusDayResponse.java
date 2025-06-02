package com.hamcam.back.dto.dashboard.stats.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BestFocusDayResponse {

    /**
     * 가장 집중을 잘한 날짜 (예: 2025-05-12)
     */
    private LocalDate bestDay;

    /**
     * 해당 날짜의 집중률 (0~100)
     */
    private int bestFocusRate;
}
