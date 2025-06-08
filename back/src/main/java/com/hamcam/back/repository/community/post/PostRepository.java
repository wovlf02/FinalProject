package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** ✅ 게시글 단건 조회 */
    Optional<Post> findById(Long postId);

    /** ✅ 전체 페이징 조회 */
    Page<Post> findAll(Pageable pageable);

    /** ✅ 카테고리별 페이징 조회 */
    Page<Post> findAllByCategory(PostCategory category, Pageable pageable);

    /** ✅ 제목 검색 */
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /** ✅ 본문 검색 */
    Page<Post> findByContentContainingIgnoreCase(String content, Pageable pageable);

    /** ✅ 작성자 닉네임 검색 */
    Page<Post> findByWriter_NicknameContainingIgnoreCase(String nickname, Pageable pageable);

    /** ✅ 제목+본문 검색 */
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable);

    /** ✅ 카테고리 + 제목 검색 */
    Page<Post> findByCategoryAndTitleContainingIgnoreCase(
            PostCategory category, String title, Pageable pageable);

    /** ✅ 카테고리 + 본문 검색 */
    Page<Post> findByCategoryAndContentContainingIgnoreCase(
            PostCategory category, String content, Pageable pageable);

    /** ✅ 카테고리 + 작성자 검색 */
    Page<Post> findByCategoryAndWriter_NicknameContainingIgnoreCase(
            PostCategory category, String nickname, Pageable pageable);

    /** ✅ 카테고리 + 제목+본문 복합 검색 (LOWER 제거 → 오류 방지) */
    @Query("""
        SELECT p FROM Post p
        WHERE 
            p.category = :category
            AND (
                p.title LIKE CONCAT('%', :keyword, '%') 
                OR p.content LIKE CONCAT('%', :keyword, '%')
            )
        ORDER BY p.createdAt DESC
    """)
    Page<Post> searchByCategoryAndKeyword(
            @Param("category") PostCategory category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /** ✅ 인기 게시글 (likeCount ≥ 20) */
    @Query("""
        SELECT p FROM Post p
        WHERE p.likeCount >= 20
        ORDER BY (p.likeCount + p.viewCount) DESC
    """)
    Page<Post> findPopularPosts(Pageable pageable);

    /** ✅ 필터링: 카테고리 없이 */
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

    /** ✅ 필터링: 카테고리 포함 */
    @Query("""
        SELECT p FROM Post p
        WHERE 
            p.category = :category
            AND (
                :keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') 
                OR p.content LIKE CONCAT('%', :keyword, '%')
            )
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
