package com.hamcam.back.repository.community.like;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Like;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * [LikeRepository]
 *
 * 커뮤니티 좋아요(Like) 관련 JPA Repository입니다.
 * - 게시글, 댓글, 대댓글의 좋아요 등록/취소/조회/카운트 처리
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    // ===== 게시글 좋아요 =====

    /**
     * [게시글 좋아요 여부 확인]
     * 특정 사용자가 해당 게시글에 좋아요를 눌렀는지 확인합니다.
     *
     * @param user 사용자
     * @param post 게시글
     * @return 좋아요 정보 (Optional)
     */
    Optional<Like> findByUserAndPost(User user, Post post);

    /**
     * [게시글 좋아요 수 조회]
     * 특정 게시글에 등록된 좋아요 수를 반환합니다.
     *
     * @param postId 게시글 ID
     * @return 좋아요 수
     */
    long countByPostId(Long postId);

    // ===== 댓글 좋아요 =====

    /**
     * [댓글 좋아요 여부 확인]
     * 특정 사용자가 해당 댓글에 좋아요를 눌렀는지 확인합니다.
     *
     * @param user 사용자
     * @param comment 댓글
     * @return 좋아요 정보 (Optional)
     */
    Optional<Like> findByUserAndComment(User user, Comment comment);

    /**
     * [댓글 좋아요 수 조회]
     * 특정 댓글에 등록된 좋아요 수를 반환합니다.
     *
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    long countByCommentId(Long commentId);

    // ===== 대댓글 좋아요 =====

    /**
     * [대댓글 좋아요 여부 확인]
     * 특정 사용자가 해당 대댓글에 좋아요를 눌렀는지 확인합니다.
     *
     * @param user 사용자
     * @param reply 대댓글
     * @return 좋아요 정보 (Optional)
     */
    Optional<Like> findByUserAndReply(User user, Reply reply);

    /**
     * [대댓글 좋아요 수 조회]
     * 특정 대댓글에 등록된 좋아요 수를 반환합니다.
     *
     * @param replyId 대댓글 ID
     * @return 좋아요 수
     */
    long countByReplyId(Long replyId);
}
