package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 신고 엔티티 (MySQL 기반)
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL 기본 키 전략
    private Long id;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReportStatus status;

    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

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

    /**
     * 올바른 단일 대상이 설정되어 있는지 확인
     */
    public boolean isValidTarget() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        if (targetUser != null) count++;
        return count == 1;
    }
}
