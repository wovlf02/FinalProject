package com.hamcam.back.dto.dashboard.todo.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [TodoDeleteRequest]
 *
 * 특정 할 일을 삭제할 때 사용하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDeleteRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 삭제할 할 일 ID
     */
    @NotNull(message = "todoId는 필수입니다.")
    private Long todoId;
}
