package com.hamcam.back.repository.community.comment;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [CommentRepository]
 *
 * 커뮤니티 댓글(Comment) 관련 JPA Repository입니다.
 * - 게시글 기준 댓글 목록 조회
 * - 댓글 수 카운트 등에 사용됩니다.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * [최신순 댓글 조회]
     */
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    /**
     * [등록순 댓글 조회]
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * [게시글 ID 기준 댓글 목록 조회]
     */
    List<Comment> findByPostId(Long postId);

    /**
     * [게시글 ID 기준 댓글 수 카운트]
     */
    long countByPostId(Long postId);
}
