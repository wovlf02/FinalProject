package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "BLOCKS",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_BLOCK_USER_POST_COMMENT_REPLY",
                columnNames = {"USER_ID", "POST_ID", "COMMENT_ID", "REPLY_ID"}
        ),
        indexes = {
                @Index(name = "IDX_BLOCK_USER", columnList = "USER_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "block_seq_generator")
    @SequenceGenerator(
            name = "block_seq_generator",
            sequenceName = "BLOCK_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 차단한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    /** 차단 대상: 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    /** 차단 대상: 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    /** 차단 대상: 대댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID")
    private Reply reply;

    /** 차단 시각 */
    @Column(name = "BLOCKED_AT", nullable = false, updatable = false)
    private LocalDateTime blockedAt;

    /** 논리 삭제 여부 */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 삭제 시각 */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

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
