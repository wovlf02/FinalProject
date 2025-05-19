package com.hamcam.back.dto.dashboard.reflection.request;

import com.hamcam.back.dto.dashboard.reflection.response.ReflectionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionReflectionRequest {

    /**
     * 회고 시작일 (yyyy-MM-dd)
     */
    @NotNull(message = "시작일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /**
     * 회고 종료일 (yyyy-MM-dd)
     */
    @NotNull(message = "종료일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /**
     * 회고 타입 (GENERAL, MOTIVATION)
     */
    @NotNull(message = "회고 타입을 선택해주세요.")
    private ReflectionType type;
}
