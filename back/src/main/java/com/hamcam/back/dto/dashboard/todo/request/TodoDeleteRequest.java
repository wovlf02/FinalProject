package com.hamcam.back.dto.dashboard.todo.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 할 일 삭제 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDeleteRequest {

    @NotNull
    private Long todoId;
}
