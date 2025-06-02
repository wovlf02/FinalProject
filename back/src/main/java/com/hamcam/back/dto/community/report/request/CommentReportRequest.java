package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 댓글 신고 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReportRequest {

    @NotNull(message = "댓글 ID는 필수입니다.")
    private Long commentId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private String report;
}
