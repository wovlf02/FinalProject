package com.hamcam.back.repository.community.comment;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 댓글(Comment) 관련 JPA Repository
 * <p>
 * 게시글에 달린 댓글을 조회하거나 댓글 수정, 삭제 등에 사용됩니다.
 * </p>
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글 기준 댓글 전체 조회 (최신순)
     *
     * @param post 대상 게시글
     * @return 댓글 리스트
     */
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    /**
     * 게시글 기준 댓글 전체 조회 (오래된 순)
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * 게시글 ID로 댓글 목록 조회
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 댓글이 속한 게시글 ID와 함께 조회
     */
    long countByPostId(Long postId);
    List<Comment> findByPostAndIsDeletedFalseOrderByCreatedAtAsc(Post post);
}
