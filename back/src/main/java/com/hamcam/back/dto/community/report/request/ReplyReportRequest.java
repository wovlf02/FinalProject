package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 대댓글 신고 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyReportRequest {

    @NotNull(message = "신고자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "대댓글 ID는 필수입니다.")
    private Long replyId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private String report;
}
