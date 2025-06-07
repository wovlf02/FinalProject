package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * [PostFavoriteRepository]
 *
 * 게시글 즐겨찾기(PostFavorite) 관련 JPA Repository입니다.
 * - 사용자와 게시글 간 N:M 즐겨찾기 관계를 관리합니다.
 */
@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {

    /**
     * [즐겨찾기 여부 확인]
     * 특정 사용자가 특정 게시글을 즐겨찾기 했는지 확인합니다.
     */
    boolean existsByUserAndPost(User user, Post post);

    /**
     * [즐겨찾기 단건 조회]
     * 사용자와 게시글로 즐겨찾기 엔티티를 조회합니다.
     */
    Optional<PostFavorite> findByUserAndPost(User user, Post post);

    /**
     * [사용자의 즐겨찾기 목록 조회]
     * 사용자가 즐겨찾기한 모든 게시글과의 관계 엔티티를 조회합니다.
     */
    List<PostFavorite> findAllByUser(User user);
}
