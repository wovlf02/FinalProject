package com.hamcam.back.dto.dashboard.todo.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * 특정 날짜의 Todo 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDateRequest {

    @NotNull
    private LocalDate date;
}
