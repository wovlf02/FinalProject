package com.hamcam.back.dto.community.report.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ReportWithUserRequest]
 * 신고 요청 시 사용자 ID와 신고 내용이 함께 전달되는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportWithUserRequest {

    /**
     * 신고자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 신고 요청 본문
     */
    @NotNull(message = "신고 내용은 필수입니다.")
    @Valid
    private ReportRequest report;
}
