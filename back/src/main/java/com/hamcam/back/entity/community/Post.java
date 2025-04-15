package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글(Post) 엔티티
 * <p>
 * 사용자가 커뮤니티에 작성한 게시글을 나타냅니다.
 * 게시글은 댓글, 좋아요, 첨부파일, 신고 등의 다양한 기능과 연결됩니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 카테고리 (예: "자유", "질문", "공지")
     */
    private String category;

    /**
     * 작성자 (User 엔티티 연관)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    /**
     * 게시글 등록 시각
     */
    private LocalDateTime createdAt;

    /**
     * 게시글 수정 시각
     */
    private LocalDateTime updatedAt;

    /**
     * 좋아요 수
     */
    private int likeCount;

    /**
     * 댓글 수
     */
    private int commentCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();
}
