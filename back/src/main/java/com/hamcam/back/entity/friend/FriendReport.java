package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 신고 엔티티 (MySQL 호환)
 */
@Entity
@Table(
        name = "friend_report",
        uniqueConstraints = @UniqueConstraint(name = "uk_reporter_reported", columnNames = {"reporter_id", "reported_id"}),
        indexes = {
                @Index(name = "idx_reporter", columnList = "reporter_id"),
                @Index(name = "idx_reported", columnList = "reported_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendReport {

    /**
     * 기본키 - MySQL에서는 AUTO_INCREMENT 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /**
     * 신고 대상자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    /**
     * 신고 사유
     */
    @Column(nullable = false, length = 500)
    private String reason;

    /**
     * 신고 시각
     */
    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    /**
     * 신고 상태 (PENDING, RESOLVED, REJECTED 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FriendReportStatus status;

    /**
     * 신고 처리한 관리자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    /**
     * 논리 삭제 여부
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 삭제 시각
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 생성 시 기본값 설정
     */
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
