package com.studymate.back.repository;

import com.studymate.back.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PostCommentRepository (댓글 리포지토리)
 * post_comments 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 게시글의 댓글 목록 조회 기능 추가
 * 특정 사용자가 작성한 댓글 조회 기능 추가
 * 특정 게시글의 댓글 조회 기능 추가
 * 특정 게시글의 모든 댓글 삭제 기능 추가
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    /**
     * 특정 게시글의 댓글 목록 조회
     * 게시글 ID를 기준으로 해당 게시글에 달린 모든 댓글을 가져옴
     * 최신순 정렬 (createdAt 기준 내림차순 정렬)
     * @param postId    조회할 게시글 ID
     * @return  해당 게시글의 댓글 목록 (최신순 정렬)
     */
    List<PostComment> findByPostPostIdOrderByCreatedAtDesc(Long postId);

    /**
     * 특정 사용자가 작성한 댓글 목록 조회
     * 사용자 ID를 기준으로 해당 사용자가 작성한 모든 댓글을 가져옴
     * 최신순 정렬 (createdAt 기준 내림차순 정렬)
     * @param userId    조회할 사용자 ID
     * @return  해당 사용자가 작성한 댓글 목록 (최신순 정렬)
     */
    List<PostComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 게시글의 댓글 개수 조회
     * 게시글 ID를 기준으로 해당 게시글에 달린 댓글 개수를 카운트
     * @param postId    댓글 개수를 조회할 게시글 ID
     * @return  해당 게시글의 댓글 개수
     */
    long countByPostPostId(Long postId);

    /**
     * 특정 게시글의 모든 댓글 삭제
     * 게시글 삭제 시 관련 댓글도 함께 삭제됨
     * @param postId    삭제할 게시글 ID
     */
    void deleteByPostPostId(Long postId);
}
