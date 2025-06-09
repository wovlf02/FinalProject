package com.hamcam.back.dto.community.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 신고 상세 조회 응답 DTO (관리자용)
 * <p>
 * 신고된 리소스(게시글, 댓글, 대댓글, 사용자 등)에 대한 상세 정보를 포함합니다.
 * 관리자가 처리 및 검토하는 데 사용됩니다.
 * </p>
 */
@Getter
@Builder
@AllArgsConstructor
public class ReportDetailResponse {

    /**
     * 신고 고유 ID
     */
    private Long reportId;

    /**
     * 신고 대상 타입 (예: POST, COMMENT, REPLY, USER)
     */
    private String type;

    /**
     * 신고 대상 ID
     */
    private Long targetId;

    /**
     * 신고자 닉네임
     */
    private String reporterNickname;

    /**
     * 신고자 사용자 ID
     */
    private Long reporterId;

    /**
     * 신고 사유
     */
    private String reason;

    /**
     * 신고 처리 상태 (예: PENDING, RESOLVED, REJECTED)
     */
    private String status;

    /**
     * 신고 접수 시각
     */
    private LocalDateTime reportedAt;

    /**
     * 관리자 메모 또는 부가 설명
     */
    private String additionalNote;
}
