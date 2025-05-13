package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 즐겨찾기 엔티티 (MySQL 기반)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "post_favorite", // ✅ 테이블 소문자
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_post", columnNames = {"user_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_favorite_user", columnList = "user_id"),
                @Index(name = "idx_favorite_post", columnList = "post_id"),
                @Index(name = "idx_favorite_is_deleted", columnList = "is_deleted")
        }
)
public class PostFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL 기본 키 전략
    private Long id;

    /**
     * 즐겨찾기한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 즐겨찾기된 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 즐겨찾기 등록 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 즐겨찾기 논리 삭제 여부
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 삭제된 시각 (soft delete 용도)
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 즐겨찾기 취소 처리 (소프트 삭제)
     */
    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    /**
     * 즐겨찾기 복원 처리
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /**
     * 유효 상태 확인
     */
    public boolean isActive() {
        return !this.isDeleted;
    }
}
