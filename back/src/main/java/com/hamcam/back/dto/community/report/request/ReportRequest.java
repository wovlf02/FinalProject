package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ReportRequest]
 * 리소스(게시글, 댓글 등)를 신고할 때 사용되는 신고 내용 DTO
 */
@Getter
@NoArgsConstructor
public class ReportRequest {

    /**
     * 신고 사유
     */
    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;

    public ReportRequest(String reason) {
        this.reason = reason;
    }
}
