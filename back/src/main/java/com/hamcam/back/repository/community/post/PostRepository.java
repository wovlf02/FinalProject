package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글(Post) 관련 JPA Repository
 * <p>
 * 게시글 기본 CRUD, 작성자 기반 조회, 카테고리별 정렬 등에 사용됩니다.
 * </p>
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 작성자 기준 게시글 전체 조회
     */
    List<Post> findByWriter(User writer);

    /**
     * 카테고리 기준 게시글 조회
     */
    List<Post> findByCategory(String category);

    /**
     * 제목 + 본문 키워드 검색 (간단 검색용)
     */
    List<Post> findByTitleContainingOrContentContaining(String keyword1, String keyword2);

    /**
     * 게시글 존재 여부 확인
     */
    Optional<Post> findById(Long postId);
}
