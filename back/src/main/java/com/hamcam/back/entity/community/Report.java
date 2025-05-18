package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 신고 엔티티 (MySQL 호환)
 */
@Entity
@Table(
        name = "report",
        indexes = {
                @Index(name = "idx_report_reporter", columnList = "reporter_id"),
                @Index(name = "idx_report_post", columnList = "post_id"),
                @Index(name = "idx_report_comment", columnList = "comment_id"),
                @Index(name = "idx_report_reply", columnList = "reply_id"),
                @Index(name = "idx_report_target_user", columnList = "target_user_id"),
                @Index(name = "idx_report_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    /**
     * 기본키 - AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고 사유
     */
    @Column(nullable = false, length = 500)
    private String reason;

    /**
     * 신고 상태 (PENDING, APPROVED, REJECTED 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportStatus status;

    /**
     * 신고 일시
     */
    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    /**
     * 신고자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /**
     * 신고 대상: 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 신고 대상: 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 신고 대상: 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /**
     * 신고 대상: 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    /**
     * 생성 시 신고 일시 및 초기 상태 설정
     */
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    // ===== 유효성 체크 메서드 =====

    public boolean isPostReport() {
        return post != null && comment == null && reply == null && targetUser == null;
    }

    public boolean isCommentReport() {
        return comment != null && post == null && reply == null && targetUser == null;
    }

    public boolean isReplyReport() {
        return reply != null && post == null && comment == null && targetUser == null;
    }

    public boolean isUserReport() {
        return targetUser != null && post == null && comment == null && reply == null;
    }

    public boolean isValidTarget() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        if (targetUser != null) count++;
        return count == 1;
    }
}
