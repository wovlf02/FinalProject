package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 댓글 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(name = "COMMENTS",
        indexes = {
                @Index(name = "IDX_COMMENT_POST", columnList = "POST_ID"),
                @Index(name = "IDX_COMMENT_WRITER", columnList = "WRITER_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_generator")
    @SequenceGenerator(
            name = "comment_seq_generator",
            sequenceName = "COMMENT_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 댓글 내용 */
    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    /** 댓글 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", nullable = false)
    private User writer;

    /** 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

    /** 생성일 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 수정일 */
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    /** 좋아요 수 */
    @Builder.Default
    @Column(name = "LIKE_COUNT", nullable = false)
    private int likeCount = 0;

    /** 좋아요 목록 */
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    /** 대댓글 목록 */
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    /** 삭제 여부 (소프트 삭제) */
    @Builder.Default
    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted = false;

    /** 삭제 시각 */
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

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

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return !isDeleted;
    }
}
