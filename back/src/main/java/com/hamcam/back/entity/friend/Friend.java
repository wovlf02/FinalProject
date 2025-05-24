package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 관계 엔티티 (MySQL 호환)
 */
@Entity
@Table(
        name = "friend",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_friend", columnNames = {"user_id", "friend_id"}),
        indexes = {
                @Index(name = "idx_friend_user", columnList = "user_id"),
                @Index(name = "idx_friend_friend", columnList = "friend_id"),
                @Index(name = "idx_friend_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    /**
     * 기본키 - MySQL용 AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 친구 요청 보낸 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 친구 요청 받은 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    /**
     * 친구 등록 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 논리 삭제 여부
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 친구 삭제 시각
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 생성 시 자동 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (!this.isDeleted) this.isDeleted = false;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 친구 삭제 처리 (소프트 삭제)
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 친구 복구 처리
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public static Friend of(User user, User friend) {
        return Friend.builder()
                .user(user)
                .friend(friend)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

}
