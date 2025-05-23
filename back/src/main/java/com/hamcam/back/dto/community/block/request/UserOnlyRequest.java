package com.hamcam.back.dto.community.block.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * userId 기반 단순 조회 요청 DTO (예: 차단 목록 조회 등)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnlyRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
