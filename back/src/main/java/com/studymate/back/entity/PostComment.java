package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * PostComment Entity (게시판 댓글)
 * 특정 게시글에 대한 댓글을 관리하는 JPA 엔티티
 * users 테이블과 연관 (작성자 정보 저장)
 * posts 테이블과 연관 (어떤 게시글에 작성된 댓글인지 저장)
 */
@Entity
@Table(name = "post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostComment {

    /**
     * 댓글 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    /**
     * 해당 댓글이 속한 게시글 (Post)
     * Many-to-One 관계 (하나의 게시글에 여러 댓글 가능)
     * 부모 게시글삭제 시, 댓글도 삭제됨 (CASCADE 설정)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_post"))
    private Post post;

    /**
     * 댓글 작성자 (User)
     * Many-to-One 관계 (한사용자가 여러 댓글 작성 가능)
     * 사용자 삭제 시, 댓글 작성자 정보는 NULL로 유지 (SET NULL)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_comment_user"))
    private User user;

    /**
     * 댓글 내용 (content)
     * 최대 500자 제한
     * Not Null (필수 값)
     */
    @Column(name = "content", updatable = false, length = 500)
    private String content;

    /**
     * 댓글 작성 시각 (created_at)
     * Default: 현재 시각
     * 댓글이 작성될 때 자동 설정
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
