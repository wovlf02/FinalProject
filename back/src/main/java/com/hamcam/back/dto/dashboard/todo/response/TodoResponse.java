package com.hamcam.back.dto.dashboard.todo.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

    /**
     * Todo ID
     */
    private Long id;

    /**
     * 제목
     */
    private String title;

    /**
     * 설명 (선택)
     */
    private String description;

    /**
     * 할 일 날짜
     */
    private LocalDate todoDate;

    /**
     * 우선순위 (1: 낮음, 2: 중간, 3: 높음)
     */
    private int priority;

    /**
     * 완료 여부
     */
    private boolean isCompleted;
}
