package com.hamcam.back.dto.community.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 신고 목록 응답 DTO (관리자용)
 * <p>
 * type(post/comment/reply/user) 및 상태(pending/resolved 등)에 따라 필터링된 신고 목록을 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class ReportListResponse {

    private List<ReportSummary> reports;

    @Data
    @AllArgsConstructor
    public static class ReportSummary {
        private Long reportId;
        private String type;          // POST, COMMENT, REPLY, USER
        private Long targetId;
        private String reporterNickname;
        private String reason;
        private String status;        // pending, resolved 등
        private LocalDateTime reportedAt;
    }
}
