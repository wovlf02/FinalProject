package com.hamcam.back.dto.dashboard.todo.request;

import com.hamcam.back.entity.dashboard.PriorityLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [TodoUpdateRequest]
 *
 * 할 일(Todo) 수정 요청 DTO
 * - 사용자 ID, 제목, 설명, 날짜, 우선순위 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoUpdateRequest {

    /**
     * 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 수정할 제목
     */
    @NotBlank(message = "제목은 필수 입력입니다.")
    private String title;

    /**
     * 수정할 설명 (선택)
     */
    private String description;

    /**
     * 수정할 날짜 (yyyy-MM-dd)
     */
    @NotNull(message = "날짜를 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    /**
     * 우선순위 (LOW, NORMAL, HIGH 중 하나)
     */
    @NotNull(message = "우선순위를 입력해주세요.")
    private PriorityLevel priority;
}
