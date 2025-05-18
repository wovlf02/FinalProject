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
 * - 삭제 여부 필터링
 * - 댓글 수 카운트 등에 사용됩니다.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * [최신순 댓글 조회]
     * 특정 게시글에 달린 댓글을 최신순으로 정렬하여 반환합니다.
     *
     * @param post 대상 게시글
     * @return 댓글 목록 (최신순)
     */
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    /**
     * [등록순 댓글 조회]
     * 특정 게시글에 달린 댓글을 오래된 순서대로 정렬하여 반환합니다.
     *
     * @param post 대상 게시글
     * @return 댓글 목록 (등록순)
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * [게시글 ID 기준 댓글 목록 조회]
     * Post 엔티티 없이 postId만으로 댓글들을 조회할 때 사용합니다.
     *
     * @param postId 게시글 ID
     * @return 해당 게시글에 달린 댓글 목록
     */
    List<Comment> findByPostId(Long postId);

    /**
     * [게시글 ID 기준 댓글 수 카운트]
     *
     * @param postId 게시글 ID
     * @return 해당 게시글에 달린 댓글 수
     */
    long countByPostId(Long postId);

    /**
     * [삭제되지 않은 댓글만 조회 - 등록순]
     * isDeleted = false 조건이 붙은 댓글만 반환하며, 등록순으로 정렬합니다.
     *
     * @param post 대상 게시글
     * @return 활성 댓글 목록 (등록순)
     */
    List<Comment> findByPostAndIsDeletedFalseOrderByCreatedAtAsc(Post post);
}
