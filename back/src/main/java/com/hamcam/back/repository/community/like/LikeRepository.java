package com.hamcam.back.repository.community.like;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Like;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 좋아요(Like) 관련 JPA Repository
 * <p>
 * 게시글, 댓글, 대댓글에 대한 좋아요 추가, 취소, 여부 확인, 카운트 등을 처리합니다.
 * </p>
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    // ===== 게시글 좋아요 =====

    /**
     * 게시글 좋아요 여부 확인
     */
    Optional<Like> findByUserAndPost(User user, Post post);

    /**
     * 게시글 ID 기준 좋아요 수 조회
     */
    long countByPostId(Long postId);

    // ===== 댓글 좋아요 =====

    /**
     * 댓글 좋아요 여부 확인
     */
    Optional<Like> findByUserAndComment(User user, Comment comment);

    /**
     * 댓글 ID 기준 좋아요 수 조회
     */
    long countByCommentId(Long commentId);

    // ===== 대댓글 좋아요 =====

    /**
     * 대댓글 좋아요 여부 확인
     */
    Optional<Like> findByUserAndReply(User user, Reply reply);

    /**
     * 대댓글 ID 기준 좋아요 수 조회
     */
    long countByReplyId(Long replyId);
}
