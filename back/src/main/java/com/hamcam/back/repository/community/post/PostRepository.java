package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게시글(Post) 관련 JPA Repository
 * <p>
 * 작성자, 카테고리, 키워드 검색, 인기글 정렬, 활동 랭킹 등 모든 기능을 통합 제공합니다.
 * </p>
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 작성자 기준 게시글 전체 조회
     */
    List<Post> findByWriter(User writer);

    /**
     * 카테고리 기준 게시글 조회 (페이징 포함)
     */
    Page<Post> findByCategory(String category, Pageable pageable);

    /**
     * 제목 또는 내용 키워드 검색 (간단 검색)
     */
    List<Post> findByTitleContainingOrContentContaining(String keyword1, String keyword2);

    /**
     * 게시글 존재 여부 확인
     */
    Optional<Post> findById(Long postId);

    /**
     * 인기 게시글 조회 (좋아요 + 조회수 기반 정렬)
     */
    @Query("SELECT p FROM Post p ORDER BY (p.likeCount + p.viewCount) DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * 활동 랭킹 기반 게시글 사용자 집계
     * 활동 점수 = 작성 게시글 수 + 좋아요 합
     */
    @Query("""
        SELECT p.writer.id, p.writer.nickname, p.writer.profileImageUrl, COUNT(p), SUM(p.likeCount)
        FROM Post p
        GROUP BY p.writer
        ORDER BY SUM(p.likeCount) DESC
    """)
    Page<Object[]> getUserPostRanking(Pageable pageable);

    /**
     * 복합 조건: 카테고리, 키워드, 좋아요 수 필터링
     * 정렬은 Pageable에서 처리
     */
    @Query("""
        SELECT p FROM Post p
        WHERE (:category IS NULL OR p.category = :category)
        AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)
        AND p.likeCount >= :minLikes
    """)
    Page<Post> searchFilteredPosts(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("minLikes") int minLikes,
            Pageable pageable
    );

}
