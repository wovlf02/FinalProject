package com.hamcam.back.repository.community.comment;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [ReplyRepository]
 *
 * 대댓글(Reply) 관련 JPA Repository입니다.
 * - 댓글 및 게시글 기준 대댓글 조회
 * - 삭제 여부 조건은 제외됨 (하드 삭제 방식)
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    /**
     * 댓글 기준 전체 대댓글 조회
     */
    List<Reply> findByComment(Comment comment);

    /**
     * 게시글 기준 전체 대댓글 조회
     */
    List<Reply> findByPost(Post post);

    /**
     * 댓글 기준 최신순 정렬 대댓글 조회
     */
    List<Reply> findByCommentOrderByCreatedAtDesc(Comment comment);

    /**
     * 댓글 ID 기준 대댓글 조회
     */
    List<Reply> findByCommentId(Long commentId);

    /**
     * 댓글 ID 기준 대댓글 수 조회
     */
    long countByCommentId(Long commentId);
}
