package com.hamcam.back.dto.dashboard.calendar;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * [CalendarEventDto]
 * 캘린더 날짜별 이벤트 DTO (할 일, 시험, 공부시간 포함)
 */
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
     * 프론트에서 문자열 비교용 (YYYY-MM-DD)
     */
    private String dateString;

    /**
     * 해당 날짜에 등록된 할 일(Todo) 제목 리스트
     */
    @Builder.Default
    private List<String> todos = new ArrayList<>();

    /**
     * 해당 날짜에 예정된 시험 제목 리스트
     */
    @Builder.Default
    private List<String> exams = new ArrayList<>();

    /**
     * 해당 날짜의 총 공부 시간 (분 단위)
     */
    @Builder.Default
    private int totalStudyMinutes = 0;

    // ✅ LocalDate만 받는 생성자 (기본값 포함)
    public CalendarEventDto(LocalDate date) {
        this.date = date;
        this.dateString = date.toString(); // 'YYYY-MM-DD' 문자열 자동 생성
        this.todos = new ArrayList<>();
        this.exams = new ArrayList<>();
        this.totalStudyMinutes = 0;
    }
}
