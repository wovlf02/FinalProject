package com.hamcam.back.dto.dashboard.calendar;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventDto {

    /**
     * 날짜 (예: 2025-05-19)
     */
    private LocalDate date;

    /**
     * 해당 날짜의 Todo 제목 리스트
     */
    private List<String> todos;

    /**
     * 해당 날짜의 시험 제목 리스트
     */
    private List<String> exams;

    /**
     * 해당 날짜의 공부 시간 (분 단위 합계)
     */
    private int totalStudyMinutes;
}
