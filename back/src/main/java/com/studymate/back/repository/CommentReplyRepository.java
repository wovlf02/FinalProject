package com.studymate.back.repository;

import com.studymate.back.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CommentReplyRepository (대댓글 리포지토리)
 * comment_replies 텡블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 댓글의 대댓글 목록 조회 기능 추가
 * 특정 사용자가 작성한 대댓글 조회 기능추가
 * 특정 댓글의 대댓글 개수 조회 기능 추가
 * 특정 댓글에 모든 대댓글 삭제 기능 추가
 */
@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {

    /**
     * 특정 댓글의 대댓글 목록 조회
     * 댓글 ID를 기준으로 해당 댓글에 달린 모든 대댓글을 가져옴
     * 최신순 정렬 (createdAt 기준 내림차순 정렬)
     * @param commentId 조회할 댓글 ID
     * @return  해당 댓글의 대댓글 목록 (최신순 정렬)
     */
    List<CommentReply> findByCommentCommentIdOrderByCreatedAtDesc(Long commentId);

    /**
     * 특정 사용자가 작성한 대댓글 목록 조회
     * 사용자 ID를 기준으로 해당 사용자가 작성한 모든 대댓글을 가져옴
     * 최신순 정렬 (createdAt 기준 내림차순 정렬)
     * @param userId    조회할 사용자 ID
     * @return  해당 사용자가 작성한 대댓글 목록 (최신순 정렬)
     */
    List<CommentReply> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 댓글의 대댓글 개수 조회
     * 댓글 ID를 기준으로 해당 댓글에 달린 대댓글 개수를 카운트
     * @param commentId 대댓글 개수를 조회할 댓글 ID
     * @return  해당 댓글의 대댓글 개수
     */
    long countByCommentCommentId(Long commentId);

    /**
     * 특정 댓글의 모든 대댓글 삭제
     * 댓글 삭제 시 관련 대댓글도 함께 삭제됨
     * @param commentId 삭제할 댓글 ID
     */
    void deleteByCommentCommentId(Long commentId);
}
