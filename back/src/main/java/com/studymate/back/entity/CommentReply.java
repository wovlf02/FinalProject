package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * CommentReply Entity (대댓글)
 * 특정댓글에 대한 대댓글을 관리하는 JPA 엔티티
 * users 테이블과 연관 (대댓글 작성자 정보 저장)
 * post_comments 테이블과 연관 (부모 댓글 정보 저장)
 */
@Entity
@Table(name = "comment_replies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReply {

    /**
     * 대댓글 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    /**
     * 부모 댓글 (PostComment)
     * Many-to-One 관계 (하나의 댓글에 여러 개의 대댓글 가능)
     * 부모 댓글 삭제 시, 대댓글도 삭제됨 (CASCADE 설정)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reply_comment"))
    private PostComment comment;

    /**
     * 대댓글 작성자 (User)
     * Many-to-One 관계 (한 사용자가 여러 개의 대댓글 작성 가능)
     * 사용자 삭제 시, 대댓글 작성자 정보는 NULL로 유지 (SET NULL)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_reply_user"))
    private User user;

    /**
     * 대댓글 내용 (content)
     * 최대 500자 제한
     * Not Null (필수 값)
     */
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    /**
     * 대댓글 작성 시각 (created_at)
     * Default: 현재 시각
     * 대댓글이 작성될 때 자동 설정
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
