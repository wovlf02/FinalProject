package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 및 대댓글 신고 요청 DTO
 * 사용자가 댓글 또는 대댓글을 신고할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentReportRequest {

    /**
     * 신고 대상 ID
     * 댓글 또는 대댓글 ID
     */
    @NotNull(message = "신고할 댓글 또는 대댓글 ID가 필요합니다.")
    private Long commentId;

    /**
     * 신고자 ID
     * 신고를 수행하는 사용자의 ID
     */
    @NotNull(message = "신고자의 ID가 필요합니다.")
    private Long reporterId;

    /**
     * 신고 사유
     * 사용자가 입력해야 하는 필수 값
     */
    @NotBlank(message = "신고 사유를 입력해야 합니다.")
    private String reason;

    /**
     * 추가 설명 (선택 사항)
     * 신고 사유에 대한 추가적인 설명
     */
    private String additionalDetails;
}
