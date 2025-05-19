package com.hamcam.back.dto.dashboard.todo.request;

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
    private LocalDate todoDate;

    /**
     * 우선순위 (1: 낮음, 2: 중간, 3: 높음)
     */
    @NotNull(message = "우선순위를 입력해주세요.")
    @Min(value = 1, message = "우선순위는 1 이상이어야 합니다.")
    @Max(value = 3, message = "우선순위는 3 이하여야 합니다.")
    private Integer priority;
}
