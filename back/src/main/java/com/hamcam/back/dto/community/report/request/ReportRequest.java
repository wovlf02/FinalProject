package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 신고 요청 DTO
 * <p>
 * 사용자가 게시글, 댓글, 대댓글, 사용자 등을 신고할 때 사용하는 요청 형식입니다.
 * 신고 대상의 ID는 URI PathVariable로 전달되며,
 * 본 DTO는 신고 사유(reason)만 포함합니다.
 * </p>
 *
 * 사용 예시:
 * <pre>
 * POST /api/community/posts/12/report
 * Body: {
 *   "reason": "욕설과 비방이 포함되어 있습니다."
 * }
 * </pre>
 */
@Getter
@NoArgsConstructor
public class ReportRequest {

    /**
     * 신고 사유 (내용 필수)
     */
    @NotBlank(message = "신고 사유는 필수 입력 값입니다.")
    private String reason;

    public ReportRequest(String reason) {
        this.reason = reason;
    }
}
