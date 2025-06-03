package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 댓글 엔티티 (하드 삭제 방식)
 */
@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id"),
                @Index(name = "idx_comment_writer", columnList = "writer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 댓글 내용 */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 댓글 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /** 소속된 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** 생성일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 수정일시 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 좋아요 수 */
    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    /** 좋아요 목록 */
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    /** 대댓글 목록 */
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    // ===== 생명주기 =====

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 =====

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
