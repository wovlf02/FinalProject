package com.hamcam.back.entity.community;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 좋아요(Likes) 엔티티
 * <p>
 * 게시글, 댓글, 대댓글에 대해 사용자가 좋아요를 누른 기록을 저장합니다.
 * 하나의 좋아요는 하나의 사용자와 하나의 대상(Post, Comment, Reply)과 연결됩니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "likes", // MySQL 예약어 'like' 피하기 위해 테이블명 변경
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id", "comment_id", "reply_id"})
)
public class Like {

    /**
     * 좋아요 식별자 (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요를 누른 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 좋아요가 눌린 게시글 (nullable = true → 댓글/대댓글과 구분용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 좋아요가 눌린 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * 좋아요가 눌린 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;
}
