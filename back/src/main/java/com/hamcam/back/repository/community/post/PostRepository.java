package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * [PostRepository]
 *
 * 커뮤니티 게시글(Post) 관련 JPA Repository입니다.
 * - 게시글 조회, 검색, 정렬, 인기글, 활동 랭킹 등을 제공합니다.
 * - 즐겨찾기는 PostFavoriteRepository에서 별도로 관리합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * [게시글 단건 조회]
     * 게시글 ID를 기준으로 단일 게시글을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글(Optional)
     */
    Optional<Post> findById(Long postId);

    /**
     * [제목 또는 본문 내 키워드 포함 게시글 검색]
     * 대소문자 구분 없이 제목 또는 본문에 특정 키워드가 포함된 게시글을 페이징하여 조회합니다.
     *
     * @param title 키워드 (제목 기준)
     * @param content 키워드 (본문 기준)
     * @param pageable 페이징 정보
     * @return 검색된 게시글 페이지
     */
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content,
            Pageable pageable
    );

    /**
     * [전체 게시글 페이징 조회]
     *
     * @param pageable 페이징 정보
     * @return 게시글 페이지
     */
    Page<Post> findAll(Pageable pageable);

    /**
     * [인기 게시글 조회]
     * 좋아요 수 + 조회수를 기준으로 내림차순 정렬된 인기 게시글을 반환합니다.
     *
     * @param pageable 페이징 정보
     * @return 인기 게시글 페이지
     */
    @Query("SELECT p FROM Post p ORDER BY (p.likeCount + p.viewCount) DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * [사용자 활동 랭킹]
     * 작성자 기준으로 게시글 수와 총 좋아요 수를 집계하여 활동 랭킹을 반환합니다.
     *
     * @param pageable 페이징 정보
     * @return Object[] 배열: [userId, nickname, profileImageUrl, postCount, totalLikeCount]
     */
    @Query("""
        SELECT p.writer.id, p.writer.nickname, p.writer.profileImageUrl, COUNT(p), SUM(p.likeCount)
        FROM Post p
        GROUP BY p.writer
        ORDER BY SUM(p.likeCount) DESC
    """)
    Page<Object[]> getUserPostRanking(Pageable pageable);

    /**
     * [카테고리 없이 키워드 및 좋아요 수로 필터링된 게시글 검색]
     * Oracle 호환성을 고려한 LIKE + 조건 검색 쿼리입니다.
     *
     * @param keyword 제목 또는 본문 키워드 (nullable)
     * @param minLikes 최소 좋아요 수
     * @param pageable 페이징 정보
     * @return 필터링된 게시글 페이지
     */
    @Query("""
        SELECT p FROM Post p
        WHERE 
            (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') 
             OR p.content LIKE CONCAT('%', :keyword, '%'))
            AND p.likeCount >= :minLikes
        ORDER BY p.createdAt DESC
    """)
    Page<Post> searchFilteredPostsWithoutCategory(
            @Param("keyword") String keyword,
            @Param("minLikes") int minLikes,
            Pageable pageable
    );

    // ✅ 카테고리 포함 필터링 쿼리 추가
    @Query("""
        SELECT p FROM Post p
        WHERE 
            p.category = :category
            AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') 
                 OR p.content LIKE CONCAT('%', :keyword, '%'))
            AND p.likeCount >= :minLikes
        ORDER BY p.createdAt DESC
    """)
    Page<Post> searchFilteredPosts(
            @Param("category") PostCategory category,
            @Param("keyword") String keyword,
            @Param("minLikes") int minLikes,
            Pageable pageable
    );
}
