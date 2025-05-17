package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 신고 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(
        name = "REPORT",
        indexes = {
                @Index(name = "IDX_REPORT_REPORTER", columnList = "REPORTER_ID"),
                @Index(name = "IDX_REPORT_POST", columnList = "POST_ID"),
                @Index(name = "IDX_REPORT_COMMENT", columnList = "COMMENT_ID"),
                @Index(name = "IDX_REPORT_REPLY", columnList = "REPLY_ID"),
                @Index(name = "IDX_REPORT_TARGET_USER", columnList = "TARGET_USER_ID"),
                @Index(name = "IDX_REPORT_STATUS", columnList = "STATUS")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq_generator")
    @SequenceGenerator(
            name = "report_seq_generator",
            sequenceName = "REPORT_SEQ",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "REASON", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 50)
    private ReportStatus status;

    @Column(name = "REPORTED_AT", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_ID", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID")
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_USER_ID")
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

    public boolean isValidTarget() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        if (targetUser != null) count++;
        return count == 1;
    }
}
