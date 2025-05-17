package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "LIKES",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_LIKE_USER_POST", columnNames = {"USER_ID", "POST_ID"}),
                @UniqueConstraint(name = "UK_LIKE_USER_COMMENT", columnNames = {"USER_ID", "COMMENT_ID"}),
                @UniqueConstraint(name = "UK_LIKE_USER_REPLY", columnNames = {"USER_ID", "REPLY_ID"})
        },
        indexes = {
                @Index(name = "IDX_LIKE_USER", columnList = "USER_ID"),
                @Index(name = "IDX_LIKE_POST", columnList = "POST_ID"),
                @Index(name = "IDX_LIKE_COMMENT", columnList = "COMMENT_ID"),
                @Index(name = "IDX_LIKE_REPLY", columnList = "REPLY_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_seq_generator")
    @SequenceGenerator(
            name = "like_seq_generator",
            sequenceName = "LIKE_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 좋아요 누른 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    /** 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    /** 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    /** 대댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID")
    private Reply reply;

    /** 좋아요 시각 */
    @Column(name = "LIKED_AT", nullable = false, updatable = false)
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
