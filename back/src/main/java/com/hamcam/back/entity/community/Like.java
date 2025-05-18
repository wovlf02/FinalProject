package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 좋아요 엔티티 (MySQL 호환)
 * - 게시글, 댓글, 대댓글 중 하나에만 연결 가능
 */
@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_like_user_post", columnNames = {"user_id", "post_id"}),
                @UniqueConstraint(name = "uk_like_user_comment", columnNames = {"user_id", "comment_id"}),
                @UniqueConstraint(name = "uk_like_user_reply", columnNames = {"user_id", "reply_id"})
        },
        indexes = {
                @Index(name = "idx_like_user", columnList = "user_id"),
                @Index(name = "idx_like_post", columnList = "post_id"),
                @Index(name = "idx_like_comment", columnList = "comment_id"),
                @Index(name = "idx_like_reply", columnList = "reply_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    /**
     * 기본키 - AUTO_INCREMENT 적용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요 누른 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 게시글 좋아요
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 댓글 좋아요
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 대댓글 좋아요
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /**
     * 좋아요 시각
     */
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    /**
     * 좋아요 시각 자동 설정
     */
    @PrePersist
    protected void onLike() {
        this.likedAt = LocalDateTime.now();
    }

    // ===== 유효성 및 타입 판별 메서드 =====

    public boolean isPostLike() {
        return post != null && comment == null && reply == null;
    }

    public boolean isCommentLike() {
        return comment != null && post == null && reply == null;
    }

    public boolean isReplyLike() {
        return reply != null && post == null && comment == null;
    }

    public boolean isValid() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        return count == 1;
    }

    public LikeType getTargetType() {
        if (isPostLike()) return LikeType.POST;
        if (isCommentLike()) return LikeType.COMMENT;
        if (isReplyLike()) return LikeType.REPLY;
        return LikeType.UNKNOWN;
    }

    /**
     * 좋아요 대상 유형
     */
    public enum LikeType {
        POST, COMMENT, REPLY, UNKNOWN
    }
}
