package com.hamcam.back.dto.community.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 신고 목록 응답 DTO (관리자용)
 * <p>
 * type(post/comment/reply/user) 및 상태(pending/resolved 등)에 따라 필터링된 신고 목록을 반환합니다.
 * </p>
 */
@Getter
@AllArgsConstructor
public class ReportListResponse {

    /**
     * 필터링된 신고 요약 리스트
     */
    private List<ReportSummary> reports;

    /**
     * 신고 요약 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReportSummary {

        /**
         * 신고 ID
         */
        private Long reportId;

        /**
         * 신고 타입 (POST, COMMENT, REPLY, USER)
         */
        private String type;

        /**
         * 신고 대상 리소스 ID
         */
        private Long targetId;

        /**
         * 신고자 닉네임
         */
        private String reporterNickname;

        /**
         * 신고 사유
         */
        private String reason;

        /**
         * 신고 상태 (예: pending, resolved)
         */
        private String status;

        /**
         * 신고 접수 시각
         */
        private LocalDateTime reportedAt;
    }
}
