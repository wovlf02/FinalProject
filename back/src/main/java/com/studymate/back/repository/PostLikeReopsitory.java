package com.studymate.back.repository;

import com.studymate.back.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PostLikeRepository (게시글 추천 리포지토리)
 * post_likes 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 특정 게시글의 좋아요 개수 조회 기능 추가
 * 특정 사용자가 게시글에 좋아요를 눌렀는지 확인하는 기능 추가
 * 사용자가 게시글 추천을 취소할 수 있도록 삭제 기능 추가
 */
@Repository
public interface PostLikeReopsitory extends JpaRepository<PostLike, Long> {

    /**
     * 특정 게시글의 추천 개수 조회
     * 게시글 ID를 기준으로 추천 개수를 카운트
     * @param postId    추천 개수를 조회할 게시글 ID
     * @return 해당 게시글의 추천 개수
     */
    long countByPostPostId(Long postId);

    /**
     * 특정 사용자가 특정 게시글에 추천을 눌렀는지 확인
     * 중복 추천 방지 기능을 구현하기 위해 사용됨
     * @param userId    추천을 눌렀는지 확인할 사용자 ID
     * @param postId    확인할 게시글 ID
     * @return  추천을 눌렀다면 true, 그렇지 않다면 false 반환
     */
    boolean existsByUserIdAndPostPostId(Long userId, Long postId);

    /**
     * 사용자가 게시글 추천을 취소
     * 특정 사용자와 특정 게시글 ID를 기준으로 추천 데이터 삭제
     * @param userId   추천을 취소할 사용자 ID
     * @param postId   추천을 취소할 게시글 ID
     */
    void deleteByUserIdAndPostPostId(Long userId, Long postId);
}
