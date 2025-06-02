package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 사용자 신고 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportRequest {

    @NotNull(message = "대상 사용자 ID는 필수입니다.")
    private Long targetUserId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private String report;
}
