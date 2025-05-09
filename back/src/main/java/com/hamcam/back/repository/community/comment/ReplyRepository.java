package com.hamcam.back.repository.community.comment;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 대댓글(Reply) 관련 JPA Repository
 * <p>
 * 댓글(Comment)에 달린 대댓글을 조회하거나 정렬할 때 사용됩니다.
 * </p>
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     * 특정 댓글에 달린 대댓글 목록 조회 (최신순)
     *
     * @param comment 대상 댓글
     * @return 대댓글 리스트
     */
    List<Reply> findByCommentOrderByCreatedAtDesc(Comment comment);

    /**
     * 댓글 ID로 대댓글 조회
     */
    List<Reply> findByCommentId(Long commentId);

    /**
     * 특정 댓글에 대한 대댓글 개수 조회
     */
    long countByCommentId(Long commentId);
}
