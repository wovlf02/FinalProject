package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 게시글(Post) 관련 JPA Repository
 * <p>
 * 작성자, 카테고리, 키워드 검색, 인기글 정렬, 활동 랭킹 등 Post 자체에 대한 기능을 제공합니다.
 * 즐겨찾기는 PostFavoriteRepository에서 관리합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 ID로 단건 조회
     */
    Optional<Post> findById(Long postId);

    /**
     * 제목 또는 본문 내 키워드 포함 게시글 조회 (대소문자 구분 없음)
     */
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content,
            Pageable pageable
    );

    /**
     * 전체 게시글 조회 (페이징)
     */
    Page<Post> findAll(Pageable pageable);

    /**
     * 인기 게시글 조회 (좋아요 수 + 조회수 기준 내림차순)
     */
    @Query("SELECT p FROM Post p ORDER BY (p.likeCount + p.viewCount) DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * 활동 랭킹 (작성자 기준: 게시글 수 + 총 좋아요 수)
     */
    @Query("""
        SELECT p.writer.id, p.writer.nickname, p.writer.profileImageUrl, COUNT(p), SUM(p.likeCount)
        FROM Post p
        GROUP BY p.writer
        ORDER BY SUM(p.likeCount) DESC
    """)
    Page<Object[]> getUserPostRanking(Pageable pageable);

    /**
     * 카테고리 없이 키워드 및 좋아요 수로 필터링된 게시글 검색 (Oracle 호환)
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
}
