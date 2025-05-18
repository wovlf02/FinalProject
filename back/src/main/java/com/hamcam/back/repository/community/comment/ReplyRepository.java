package com.hamcam.back.repository.community.comment;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * [ReplyRepository]
 *
 * 댓글(Comment)에 달린 대댓글(Reply) 관련 JPA Repository입니다.
 * - 대댓글 목록 조회
 * - 댓글/게시글 기준 필터링
 * - 삭제 여부 고려한 조회 등에 사용됩니다.
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     * [대댓글 최신순 조회]
     * 특정 댓글에 달린 대댓글을 최신순으로 조회합니다.
     *
     * @param comment 대상 댓글
     * @return 대댓글 리스트 (최신순)
     */
    List<Reply> findByCommentOrderByCreatedAtDesc(Comment comment);

    /**
     * [댓글 ID 기준 대댓글 조회]
     * 댓글 ID만으로 대댓글들을 조회할 때 사용합니다.
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글에 달린 대댓글 리스트
     */
    List<Reply> findByCommentId(Long commentId);

    /**
     * [댓글 ID 기준 대댓글 수 카운트]
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글에 달린 대댓글 개수
     */
    long countByCommentId(Long commentId);

    /**
     * [게시글 기준 삭제되지 않은 대댓글 조회]
     * 게시글에 포함된 모든 대댓글 중 삭제되지 않은 항목만 조회합니다.
     * (댓글 - 대댓글 함께 삭제 처리된 경우 필터링에 활용됨)
     *
     * @param post 대상 게시글
     * @return 활성 대댓글 리스트
     */
    @Query("SELECT r FROM Reply r WHERE r.post = :post AND r.isDeleted = false")
    List<Reply> findByPostAndIsDeletedFalse(@Param("post") Post post);
}
