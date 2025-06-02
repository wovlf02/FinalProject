package com.hamcam.back.dto.dashboard.reflection.request;

import com.hamcam.back.dto.dashboard.reflection.response.ReflectionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [OptionReflectionRequest]
 *
 * 옵션 기반 회고 요청 DTO
 * - 사용자 ID, 회고 기간, 회고 타입 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionReflectionRequest {

    @NotNull(message = "시작일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "회고 타입을 선택해주세요.")
    private ReflectionType type;
}
