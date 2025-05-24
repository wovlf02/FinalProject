package com.hamcam.back.dto.dashboard.stats.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * [FocusTimeUpdateRequest]
 *
 * 사용자가 특정 날짜의 공부 시간을 직접 입력 또는 수정할 수 있도록 하는 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusTimeUpdateRequest {

    @NotNull(message = "날짜는 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @Min(value = 0, message = "공부 시간은 0분 이상이어야 합니다.")
    private int studyMinutes;
}
