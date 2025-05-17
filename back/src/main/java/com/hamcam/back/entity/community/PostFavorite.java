package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글 즐겨찾기 엔티티 (Oracle Express 호환)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "POST_FAVORITE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_USER_POST", columnNames = {"USER_ID", "POST_ID"})
        },
        indexes = {
                @Index(name = "IDX_FAVORITE_USER", columnList = "USER_ID"),
                @Index(name = "IDX_FAVORITE_POST", columnList = "POST_ID"),
                @Index(name = "IDX_FAVORITE_IS_DELETED", columnList = "IS_DELETED")
        }
)
public class PostFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_favorite_seq_generator")
    @SequenceGenerator(
            name = "post_favorite_seq_generator",
            sequenceName = "POST_FAVORITE_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 즐겨찾기한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    /** 즐겨찾기된 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    /** 즐겨찾기 등록 시각 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 즐겨찾기 논리 삭제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 삭제된 시각 (soft delete 용도) */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** 즐겨찾기 취소 처리 (소프트 삭제) */
    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    /** 즐겨찾기 복원 처리 */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /** 유효 상태 확인 */
    public boolean isActive() {
        return !this.isDeleted;
    }
}
