package com.hamcam.back.dto.dashboard.todo.request;

import com.hamcam.back.entity.dashboard.PriorityLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoUpdateRequest {

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
     * 수정할 날짜
     */
    @NotNull(message = "날짜를 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate todoDate;

    /**
     * 우선순위 (LOW, NORMAL, HIGH)
     */
    @NotNull(message = "우선순위를 입력해주세요.")
    private PriorityLevel priority;
}
