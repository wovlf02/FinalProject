package com.hamcam.back.dto.dashboard.todo.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 할 일 완료 상태 토글 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoToggleRequest {

    @NotNull
    private Long todoId;
}
