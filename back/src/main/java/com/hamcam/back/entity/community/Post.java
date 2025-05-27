package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.StudyRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post",
        indexes = {
                @Index(name = "idx_post_writer", columnList = "writer_id"),
                @Index(name = "idx_post_created", columnList = "created_at")
                // ❌ idx_post_is_deleted 제거됨
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 제목 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 게시글 카테고리 (예: INFO, QUESTION, STUDY 등) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostCategory category;

    /** 게시글 본문 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 태그 (쉼표로 구분된 문자열) */
    @Column(name = "tag", length = 500)
    private String tag;

    /** 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /** 질문용 팀방 연동 (선택) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_room_id")
    private StudyRoom studyRoom;

    /** 생성일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 수정일시 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 좋아요 수 */
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    /** 조회수 */
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    /** 댓글 수 */
    @Column(name = "comment_count", nullable = false)
    private int commentCount;

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

    // ===== 생명주기 =====

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.likeCount = 0;
        this.viewCount = 0;
        this.commentCount = 0;
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
}
