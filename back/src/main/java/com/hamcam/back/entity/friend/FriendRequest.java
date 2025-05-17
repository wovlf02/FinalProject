package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 요청 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(
        name = "FRIEND_REQUEST",
        uniqueConstraints = @UniqueConstraint(name = "UK_SENDER_RECEIVER", columnNames = {"SENDER_ID", "RECEIVER_ID"}),
        indexes = {
                @Index(name = "IDX_SENDER", columnList = "SENDER_ID"),
                @Index(name = "IDX_RECEIVER", columnList = "RECEIVER_ID"),
                @Index(name = "IDX_STATUS", columnList = "STATUS")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_request_seq_generator")
    @SequenceGenerator(
            name = "friend_request_seq_generator",
            sequenceName = "FRIEND_REQUEST_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 요청 보낸 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID", nullable = false)
    private User sender;

    /** 요청 받은 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_ID", nullable = false)
    private User receiver;

    /** 요청 생성 시각 */
    @Column(name = "REQUESTED_AT", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    /** 응답 시각 */
    @Column(name = "RESPONDED_AT")
    private LocalDateTime respondedAt;

    /** 요청 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private FriendRequestStatus status;

    /** 논리 삭제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 삭제 시각 */
    @Column(name = "DELETED_AT")
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
