package com.hamcam.back.dto.dashboard.todo.request;

import com.hamcam.back.entity.dashboard.PriorityLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    /**
     * 할 일 제목
     */
    @NotBlank(message = "제목은 필수 입력입니다.")
    private String title;

    /**
     * 할 일 설명 (선택)
     */
    private String description;

    /**
     * 할 일 날짜
     */
    @NotNull(message = "날짜를 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    /**
     * 우선순위 (LOW, NORMAL, HIGH)
     */
    @NotNull(message = "우선순위를 입력해주세요.")
    private PriorityLevel priority;
}
