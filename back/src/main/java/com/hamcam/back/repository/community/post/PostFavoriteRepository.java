package com.hamcam.back.repository.community.post;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 즐겨찾기(PostFavorite) 관련 Repository
 * <p>
 * 사용자와 게시글 사이의 N:M 즐겨찾기 관계를 관리합니다.
 */
@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {

    /**
     * 특정 유저가 특정 게시글을 즐겨찾기했는지 여부
     */
    boolean existsByUserAndPost(User user, Post post);

    /**
     * 유저와 게시글로 즐겨찾기 단건 조회
     */
    @Query("SELECT f FROM PostFavorite f WHERE f.user = :user AND f.post = :post")
    Optional<PostFavorite> findByUserAndPost(@Param("user") User user, @Param("post") Post post);

    /**
     * 유저의 모든 즐겨찾기 엔티티 목록 조회
     */
    List<PostFavorite> findAllByUser(User user);
}
