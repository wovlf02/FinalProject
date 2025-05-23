package com.hamcam.back.dto.dashboard.todo.request;

import com.hamcam.back.entity.dashboard.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * 할 일 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoUpdateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "todoId는 필수입니다.")
    private Long todoId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate todoDate;

    @NotNull(message = "우선순위는 필수입니다.")
    private PriorityLevel priority;
}
