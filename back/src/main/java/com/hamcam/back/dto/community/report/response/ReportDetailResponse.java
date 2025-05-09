package com.hamcam.back.dto.community.report.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 신고 상세 조회 응답 DTO (관리자용)
 */
@Data
public class ReportDetailResponse {

    private Long reportId;
    private String type;              // 신고 타입 (POST, COMMENT, REPLY, USER)
    private Long targetId;
    private String reporterNickname;
    private Long reporterId;
    private String reason;
    private String status;            // 신고 처리 상태
    private LocalDateTime reportedAt;
    private String additionalNote;    // 관리자가 처리 중 남긴 메모 등
}
