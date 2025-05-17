package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 신고 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(
        name = "FRIEND_REPORT",
        uniqueConstraints = @UniqueConstraint(name = "UK_REPORTER_REPORTED", columnNames = {"REPORTER_ID", "REPORTED_ID"}),
        indexes = {
                @Index(name = "IDX_REPORTER", columnList = "REPORTER_ID"),
                @Index(name = "IDX_REPORTED", columnList = "REPORTED_ID"),
                @Index(name = "IDX_STATUS", columnList = "STATUS")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendReport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_report_seq_generator")
    @SequenceGenerator(
            name = "friend_report_seq_generator",
            sequenceName = "FRIEND_REPORT_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 신고자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_ID", nullable = false)
    private User reporter;

    /** 신고 대상자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTED_ID", nullable = false)
    private User reported;

    /** 신고 사유 */
    @Column(name = "REASON", nullable = false, length = 500)
    private String reason;

    /** 신고 시각 */
    @Column(name = "REPORTED_AT", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    /** 신고 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 50)
    private FriendReportStatus status;

    /** 관리자 처리자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESOLVED_BY")
    private User resolvedBy;

    /** 삭제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 삭제 시각 */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (this.reportedAt == null) {
            this.reportedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = FriendReportStatus.PENDING;
        }
        if (!this.isDeleted) {
            this.isDeleted = false;
        }
    }

    // === 비즈니스 로직 ===

    public void resolve(User admin) {
        this.status = FriendReportStatus.RESOLVED;
        this.resolvedBy = admin;
    }

    public void reject(User admin) {
        this.status = FriendReportStatus.REJECTED;
        this.resolvedBy = admin;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }
}
