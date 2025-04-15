package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시글 복합 조건 조회용 Query DSL / JPQL 기반 Custom Repository
 * <p>
 * 카테고리, 좋아요 수, 키워드, 정렬 방식 등 다양한 조건을 조합하여 게시글을 조회합니다.
 * </p>
 */
public interface PostQueryRepository {

    /**
     * 게시글 복합 필터링 + 정렬 + 페이징 조회
     *
     * @param category 카테고리 (nullable)
     * @param keyword 키워드 (nullable)
     * @param minLikes 최소 좋아요 수
     * @param sortType 정렬 기준 (예: recent, popular)
     * @param pageable 페이징 정보
     * @return 조건에 맞는 게시글 페이지
     */
    Page<Post> searchPosts(
            String category,
            String keyword,
            int minLikes,
            String sortType,
            Pageable pageable
    );

    /**
     * 인기 게시글 조회 (좋아요 + 조회수 기반 정렬)
     */
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * 활동 랭킹 기반 게시글 사용자 집계
     */
    Page<Object[]> getUserPostRanking(Pageable pageable);
}
