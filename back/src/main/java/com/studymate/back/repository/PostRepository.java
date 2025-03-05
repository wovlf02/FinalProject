package com.studymate.back.repository;

import com.studymate.back.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PostRepository (게시글 리포지토리)
 * posts 테이블과 연동되는 JPA Repository
 * 기본적인 CRUD 기능 제공
 * 사용자별 게시글 조회, 키워드 검색, 최신순/추천순 정렬 기능 추가
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 특정 사용자가 작성한 게시글 목록 조회
     * @param userId    조회할 사용자 ID
     * @return  해당 사용자가 작성한 게시글 목록 (최신순 정렬)
     */
    List<Post> findByUserIdOrderBycreatedAtDesc(Long userId);

    /**
     * 게시글 제목 검색 (키워드 포함)
     * @param keyword   검색할 키워드
     * @return  제목에 키워드가 포함된 게시글 목록 (최신순 정렬)
     */
    List<Post> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    /**
     * 게시글 내용 검색 (키워드 포함)
     * @param keyword   검색할 키워드
     * @return  내용에 키워드가 포함된 게시글 목록 (최신순 정렬)
     */
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    /**
     * 게시글 상세 조회
     * @param postId    조회할 게시글 ID
     * @return  Optional<Post> 객체 반환 (존재하지 않을 경우 Optional.empty())
     */
    Optional<Post> findById(Long postId);

    /**
     * 최신 게시글 목록 조회 (페이지네이션)
     * @param pageable  페이징 정보 (페이지 번호, 사이즈)
     * @return  페이지 단위의 최신 게시글 목록
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 좋아요 순 게시글 목록 조회 (페이지네이션)
     * 게시글별 추천 개수를 기준으로 내림차순 정렬
     * @param pageable  페이징 정보 (페이지 번호, 사이즈)
     * @return  추천이 많은 순서대로 정렬된 게시글 목록
     */
    @Query("SELECT p FROM Post p LEFT JOIN PostLike pl ON p.postId = pl.post.postId " +
    "GROUP BY p.postId ORDER BY COUNT(pl.post.postId) DESC")
    Page<Post> findAllByOrderByLikesDesc(Pageable pageable);
}
