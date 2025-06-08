package com.hamcam.back.dto.dashboard.todo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hamcam.back.entity.dashboard.PriorityLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [TodoRequest]
 *
 * 새로운 할 일(Todo) 등록 요청 DTO
 * - 사용자 ID, 제목, 설명, 날짜, 우선순위를 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    @NotBlank(message = "제목은 필수 입력입니다.")
    private String title;

    private String description;

    @NotNull(message = "날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @JsonProperty("date")
    private LocalDate todoDate;

    @NotNull(message = "우선순위를 입력해주세요.")
    private PriorityLevel priority;
}
