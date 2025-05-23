package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 차단 엔티티
 * - 게시글, 댓글, 대댓글, 사용자 중 하나를 차단 가능
 */
@Entity
@Table(
        name = "blocks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_block_user_targets",
                columnNames = {"user_id", "post_id", "comment_id", "reply_id", "blocked_user_id"}
        ),
        indexes = @Index(name = "idx_block_user", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 차단한 사용자 (차단 실행자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 차단 대상: 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 차단 대상: 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 차단 대상: 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /**
     * 차단 대상: 사용자 (User to User 차단)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;

    /**
     * 차단 시각
     */
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private LocalDateTime blockedAt;

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

    /**
     * 차단 시각 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }

    /**
     * 논리 삭제 처리
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 논리 삭제 복구
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /**
     * 차단 대상 유형 반환
     */
    public BlockType getBlockType() {
        if (isPostBlock()) return BlockType.POST;
        if (isCommentBlock()) return BlockType.COMMENT;
        if (isReplyBlock()) return BlockType.REPLY;
        if (isUserBlock()) return BlockType.USER;
        return BlockType.UNKNOWN;
    }

    public boolean isPostBlock() {
        return post != null && comment == null && reply == null && blockedUser == null;
    }

    public boolean isCommentBlock() {
        return comment != null && post == null && reply == null && blockedUser == null;
    }

    public boolean isReplyBlock() {
        return reply != null && post == null && comment == null && blockedUser == null;
    }

    public boolean isUserBlock() {
        return blockedUser != null && post == null && comment == null && reply == null;
    }

    /**
     * 유효한 차단인지 검사 (정확히 하나만 설정)
     */
    public boolean isInvalid() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        if (blockedUser != null) count++;
        return count != 1;
    }

    /**
     * 차단 타입 열거형
     */
    public enum BlockType {
        POST, COMMENT, REPLY, USER, UNKNOWN
    }
}
