package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 좋아요 엔티티 (게시글, 댓글, 대댓글에 대한 유저 좋아요)
 */
@Entity
@Table(
        name = "likes", // ✅ 소문자 테이블명
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL 기본 전략
    private Long id;

    /** 좋아요 누른 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /** 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /** 대댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /** 좋아요 시각 */
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onLike() {
        this.likedAt = LocalDateTime.now();
    }

    // ===== 유효성/타입 보조 메서드 =====

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

    public enum LikeType {
        POST, COMMENT, REPLY, UNKNOWN
    }
}
