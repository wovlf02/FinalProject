package com.hamcam.back.dto.community.report.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 게시글 신고 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReportRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private String report;
}
