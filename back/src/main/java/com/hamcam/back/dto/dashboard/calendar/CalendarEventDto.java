package com.hamcam.back.dto.dashboard.calendar;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * [CalendarEventDto]
 * 캘린더 날짜별 이벤트 DTO (할 일, 시험, 공부시간 포함)
 */
@Getter
@Setter // ✅ 추가됨
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventDto {

    /**
     * 날짜 (예: 2025-05-19)
     */
    private LocalDate date;

    /**
     * 해당 날짜에 등록된 할 일(Todo) 제목 리스트
     */
    private List<String> todos;

    /**
     * 해당 날짜에 예정된 시험 제목 리스트
     */
    private List<String> exams;

    /**
     * 해당 날짜의 총 공부 시간 (분 단위)
     */
    private int totalStudyMinutes;
}
