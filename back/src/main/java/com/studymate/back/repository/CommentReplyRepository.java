package com.studymate.back.repository;

import com.studymate.back.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * CommentReplyRepository (대댓글 리포지토리)
 * comment_replies 텡블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 댓글의 대댓글 목록 조회 기능 추가
 * 특정 사용자가 작성한 대댓글 조회 기능추가
 *
 */
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {

    List<CommentReply> findByCommentCommentIdOrderByCreatedAtDesc(Long commentId);

    List<CommentReply> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByCommentCommentId(Long commentId);

    void deleteByCommentCommentId(Long commentId);
}
