package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.repository.community.post.PostQueryRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글(Post) 관련 서비스
 * <p>
 * 게시글 생성, 수정, 삭제, 상세/목록 조회, 자동완성, 즐겨찾기, 인기글/랭킹 기능을 포함합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    // 추후 인증 정보에서 가져올 사용자 ID
    private Long getCurrentUserId() {
        return 1L;
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    // 게시글 생성
    public Long createPost(PostCreateRequest request) {
        Post post = Post.builder()
                .writer(getCurrentUser())
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .build();
        return postRepository.save(post).getId();
    }

    // 게시글 수정
    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = getPostOrThrow(postId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = getPostOrThrow(postId);
        postRepository.delete(post);
    }

    // 게시글 상세 조회
    public PostResponse getPostDetail(Long postId) {
        Post post = getPostOrThrow(postId);
        return PostResponse.from(post);
    }

    // 게시글 목록 조회
    public PostListResponse getPostList(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = (category != null)
                ? postRepository.findByCategory(category, pageable)
                : postRepository.findAll(pageable);
        return PostListResponse.from(posts);
    }

    // 키워드 검색
    public PostListResponse searchPosts(String keyword, String category) {
        List<Post> result = postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
        if (category != null) {
            result = result.stream()
                    .filter(p -> category.equals(p.getCategory()))
                    .collect(Collectors.toList());
        }
        return PostListResponse.from(result);
    }

    // 조건별 필터링
    public PostListResponse filterPosts(String category, String sort, int minLikes, String keyword) {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> result = postQueryRepository.searchPosts(category, keyword, minLikes, sort, pageable);
        return PostListResponse.from(result);
    }

    // 인기 게시글
    public PopularPostListResponse getPopularPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postQueryRepository.findPopularPosts(pageable);
        return PopularPostListResponse.from(result.getContent());
    }

    // 활동 랭킹
    public RankingResponse getPostRanking() {
        Page<Object[]> rankingData = postQueryRepository.getUserPostRanking(PageRequest.of(0, 10));
        return RankingResponse.from(rankingData.getContent());
    }

    // AI 기반 자동완성
    public PostAutoFillResponse autoFillPost(ProblemReferenceRequest request) {
        // 가짜 구현. 실제로는 OpenAI, 내부 문제 분석기 연동
        String title = "추천 제목: " + request.getProblemTitle();
        String content = "해당 문제는 " + request.getCategory() + "에 속하며, 해결 전략은 다음과 같습니다...";
        return PostAutoFillResponse.builder().title(title).content(content).build();
    }

    // 즐겨찾기
    public void favoritePost(Long postId) {
        // 즐겨찾기 테이블이 있다면 save (생략)
    }

    public void unfavoritePost(Long postId) {
        // 즐겨찾기 삭제 (생략)
    }

    public FavoritePostListResponse getFavoritePosts() {
        // 현재 사용자의 즐겨찾기 조회 로직 (가짜 응답)
        return new FavoritePostListResponse(List.of()); // 추후 Favorite 엔티티 연결
    }

    // ============ 유틸 ============

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }
}
