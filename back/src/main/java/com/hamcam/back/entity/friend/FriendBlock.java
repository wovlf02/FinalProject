package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 차단 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(
        name = "FRIEND_BLOCK",
        uniqueConstraints = @UniqueConstraint(name = "UK_BLOCKER_BLOCKED", columnNames = {"BLOCKER_ID", "BLOCKED_ID"}),
        indexes = {
                @Index(name = "IDX_BLOCKER", columnList = "BLOCKER_ID"),
                @Index(name = "IDX_BLOCKED", columnList = "BLOCKED_ID"),
                @Index(name = "IDX_BLOCK_IS_DELETED", columnList = "IS_DELETED")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_block_seq_generator")
    @SequenceGenerator(
            name = "friend_block_seq_generator",
            sequenceName = "FRIEND_BLOCK_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 차단한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKER_ID", nullable = false)
    private User blocker;

    /** 차단당한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKED_ID", nullable = false)
    private User blocked;

    /** 차단 시각 */
    @Column(name = "BLOCKED_AT", nullable = false, updatable = false)
    private LocalDateTime blockedAt;

    /** 차단 해제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 차단 해제 시각 */
    @Column(name = "UNBLOCKED_AT")
    private LocalDateTime unblockedAt;

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
        if (!this.isDeleted) this.isDeleted = false;
    }

    // ===== 비즈니스 로직 =====

    public void unblock() {
        this.isDeleted = true;
        this.unblockedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.unblockedAt = null;
    }
}
