package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시글/댓글/대댓글 차단 엔티티 (MySQL 기반)
 */
@Entity
@Table(
        name = "blocks", // ✅ MySQL에서는 소문자 테이블명 선호
        uniqueConstraints = @UniqueConstraint(
                name = "uk_block_user_post_comment_reply",
                columnNames = {"user_id", "post_id", "comment_id", "reply_id"}
        ),
        indexes = {
                @Index(name = "idx_block_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ MySQL 기본 키 전략
    private Long id;

    /** 차단한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 차단 대상: 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /** 차단 대상: 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /** 차단 대상: 대댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    /** 차단 시각 */
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private LocalDateTime blockedAt;

    /** 논리 삭제 여부 */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /** 삭제 시각 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }

    /**
     * 소프트 삭제 처리
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 차단 복원 처리
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /**
     * 차단 유형 반환
     */
    public BlockType getBlockType() {
        if (isPostBlock()) return BlockType.POST;
        if (isCommentBlock()) return BlockType.COMMENT;
        if (isReplyBlock()) return BlockType.REPLY;
        return BlockType.UNKNOWN;
    }

    public boolean isPostBlock() {
        return post != null && comment == null && reply == null;
    }

    public boolean isCommentBlock() {
        return comment != null && post == null && reply == null;
    }

    public boolean isReplyBlock() {
        return reply != null && post == null && comment == null;
    }

    /**
     * 차단 객체 유효성 검사 (1개 대상만 있을 때 유효)
     */
    public boolean isInvalid() {
        int count = 0;
        if (post != null) count++;
        if (comment != null) count++;
        if (reply != null) count++;
        return count != 1;
    }

    public enum BlockType {
        POST, COMMENT, REPLY, UNKNOWN
    }
}
