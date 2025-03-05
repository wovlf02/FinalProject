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



}
