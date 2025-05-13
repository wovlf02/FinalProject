package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 요청 엔티티 (MySQL 기반)
 */
@Entity
@Table(
        name = "friend_request", // ✅ 테이블명 소문자
        uniqueConstraints = @UniqueConstraint(name = "uk_sender_receiver", columnNames = {"sender_id", "receiver_id"}),
        indexes = {
                @Index(name = "idx_sender", columnList = "sender_id"),
                @Index(name = "idx_receiver", columnList = "receiver_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL 자동 증가 키
    private Long id;

    /**
     * 요청 보낸 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * 요청 받은 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * 요청 생성 시각
     */
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    /**
     * 응답 시각
     */
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    /**
     * 요청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendRequestStatus status;

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

    @PrePersist
    protected void onCreate() {
        if (this.requestedAt == null) this.requestedAt = LocalDateTime.now();
        if (this.status == null) this.status = FriendRequestStatus.PENDING;
        this.isDeleted = false;
    }

    // ===== 비즈니스 로직 =====

    public void accept() {
        if (this.status != FriendRequestStatus.PENDING) return;
        this.status = FriendRequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != FriendRequestStatus.PENDING) return;
        this.status = FriendRequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    // ===== 상태 유틸 =====

    public boolean isPending() {
        return this.status == FriendRequestStatus.PENDING;
    }

    public boolean isAccepted() {
        return this.status == FriendRequestStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return this.status == FriendRequestStatus.REJECTED;
    }
}
