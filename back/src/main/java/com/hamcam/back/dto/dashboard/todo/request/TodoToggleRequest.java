package com.hamcam.back.dto.dashboard.todo.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [TodoToggleRequest]
 *
 * 특정 할 일의 완료 상태를 토글할 때 사용하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoToggleRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 토글할 할 일 ID
     */
    @NotNull(message = "todoId는 필수입니다.")
    private Long todoId;
}
