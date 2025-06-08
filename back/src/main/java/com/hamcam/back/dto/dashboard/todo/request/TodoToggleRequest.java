package com.hamcam.back.dto.dashboard.todo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 할 일 완료 상태 토글 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TodoToggleRequest {

    @NotNull(message = "Todo ID는 필수입니다.")
    @JsonProperty("todoId")
    private Long todoId;
}
