package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커뮤니티 게시글 엔티티 (MySQL 호환)
 */
@Entity
@Table(name = "post",
        indexes = {
                @Index(name = "idx_post_writer", columnList = "writer_id"),
                @Index(name = "idx_post_created", columnList = "created_at"),
                @Index(name = "idx_post_is_deleted", columnList = "is_deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    /**
     * 게시글 ID (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 본문 내용 (MySQL에선 TEXT 타입으로 매핑됨)
     */
    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /**
     * 작성일
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 좋아요 수
     */
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    /**
     * 조회수
     */
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    /**
     * 댓글 수
     */
    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    /**
     * 삭제 여부 (소프트 삭제)
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 삭제 시각
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ===== 연관관계 =====

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFavorite> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocks = new ArrayList<>();

    // ===== 엔티티 생명주기 =====

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.likeCount = 0;
        this.viewCount = 0;
        this.commentCount = 0;
        this.isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 =====

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return !isDeleted;
    }
}
