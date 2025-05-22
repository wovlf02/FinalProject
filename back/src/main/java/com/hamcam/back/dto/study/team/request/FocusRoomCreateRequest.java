package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * [FocusRoomCreateRequest]
 * 공부시간 경쟁방(FOCUS) 생성 시 사용하는 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusRoomCreateRequest {

    @NotNull(message = "생성자 ID는 필수입니다.")
    private Long userId;

    @NotBlank(message = "방 제목은 필수입니다.")
    private String title;

    @NotNull(message = "목표 공부 시간은 필수입니다.")
    @Min(value = 10, message = "최소 목표 시간은 10분 이상이어야 합니다.")
    private Integer targetMinutes;

    private String password;

    @Min(value = 1, message = "최소 인원은 1명 이상이어야 합니다.")
    @Max(value = 20, message = "최대 인원은 20명입니다.")
    private int maxParticipants;
}
