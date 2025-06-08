package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 차단 엔티티 (MySQL 호환)
 */
@Entity
@Table(
        name = "friend_block",
        uniqueConstraints = @UniqueConstraint(name = "uk_blocker_blocked", columnNames = {"blocker_id", "blocked_id"}),
        indexes = {
                @Index(name = "idx_blocker", columnList = "blocker_id"),
                @Index(name = "idx_blocked", columnList = "blocked_id"),
                @Index(name = "idx_block_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendBlock {

    /**
     * 기본키 - MySQL에서는 AUTO_INCREMENT 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 차단한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    /**
     * 차단당한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    /**
     * 차단 시각
     */
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private LocalDateTime blockedAt;

    /**
     * 논리 삭제 여부 (차단 해제 여부)
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 차단 해제 시각
     */
    @Column(name = "unblocked_at")
    private LocalDateTime unblockedAt;

    /**
     * 생성 시 차단 시각 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
        if (!this.isDeleted) this.isDeleted = false;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 차단 해제 처리
     */
    public void unblock() {
        this.isDeleted = true;
        this.unblockedAt = LocalDateTime.now();
    }

    /**
     * 차단 복원 처리
     */
    public void restore() {
        this.isDeleted = false;
        this.unblockedAt = null;
    }
}
