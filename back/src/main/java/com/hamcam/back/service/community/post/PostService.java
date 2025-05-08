package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostFavorite;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.community.post.PostFavoriteRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.security.auth.CustomUserDetails;
import com.hamcam.back.service.community.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final AttachmentService attachmentService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new CustomException("사용자 정보를 불러올 수 없습니다.");
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }


    /**
     * 게시글 생성
     * - 게시글을 저장하고 첨부파일이 있으면 함께 저장
     */
    public Long createPost(PostCreateRequest request, MultipartFile[] files) {
        Post post = Post.builder()
                .writer(getCurrentUser())
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .build();
        post = postRepository.save(post);

        if (files != null && files.length > 0) {
            attachmentService.uploadPostFiles(post.getId(), files);
        }

        return post.getId();
    }

    /**
     * 게시글 수정
     * - 본문 및 카테고리 업데이트, 첨부파일 삭제 및 추가
     */
    public void updatePost(Long postId, PostUpdateRequest request, MultipartFile[] files) {
        Post post = getPostOrThrow(postId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        if (request.getDeleteFileIds() != null) {
            request.getDeleteFileIds().forEach(attachmentService::deleteAttachment);
        }

        if (files != null && files.length > 0) {
            attachmentService.uploadPostFiles(post.getId(), files);
        }
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(Long postId) {
        Post post = getPostOrThrow(postId);
        postRepository.delete(post);
    }

    /**
     * 게시글 상세 조회
     */
    public PostResponse getPostDetail(Long postId) {
        Post post = getPostOrThrow(postId);
        return PostResponse.from(post);
    }

    /**
     * 게시글 목록 조회 (카테고리 필터링 및 페이징 포함)
     */
    public PostListResponse getPostList(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = (category != null)
                ? postRepository.findByCategory(category, pageable)
                : postRepository.findAll(pageable);
        return PostListResponse.from(posts);
    }

    /**
     * 키워드 및 카테고리 기반 검색 (페이징 포함)
     */
    public PostListResponse searchPosts(String keyword, String category, Pageable pageable) {
        Page<Post> result;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (!hasKeyword && !hasCategory) {
            result = postRepository.findAll(pageable);
        } else if (!hasKeyword) {
            result = postRepository.findByCategory(category, pageable);
        } else if (!hasCategory) {
            result = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            Page<Post> base = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
            List<Post> filtered = base.stream()
                    .filter(p -> category.equals(p.getCategory()))
                    .toList();
            result = new PageImpl<>(filtered, pageable, filtered.size());
        }

        return PostListResponse.from(result);
    }

    /**
     * 카테고리, 정렬 기준, 최소 좋아요 수, 키워드 기반 필터링
     */
    public PostListResponse filterPosts(String category, String sort, int minLikes, String keyword) {
        Sort sortOption = "popular".equals(sort)
                ? Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("viewCount"))
                : Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(0, 20, sortOption);
        Page<Post> result = postRepository.searchFilteredPosts(category, keyword, minLikes, pageable);
        return PostListResponse.from(result);
    }

    /**
     * 인기 게시글 조회 (좋아요 + 조회수 기준 상위 10개)
     */
    public PopularPostListResponse getPopularPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findPopularPosts(pageable);
        return PopularPostListResponse.from(result.getContent());
    }

    /**
     * 활동 랭킹 조회 (작성 수 + 좋아요 합 기준 상위 10명)
     */
    public RankingResponse getPostRanking() {
        Page<Object[]> rankingData = postRepository.getUserPostRanking(PageRequest.of(0, 10));
        return RankingResponse.from(rankingData.getContent());
    }

    /**
     * AI 기반 자동완성 (가짜 구현)
     */
    public PostAutoFillResponse autoFillPost(ProblemReferenceRequest request) {
        String title = "추천 제목: " + request.getProblemTitle();
        String content = "해당 문제는 " + request.getCategory() + "에 속하며, 해결 전략은 다음과 같습니다...";
        return PostAutoFillResponse.builder().title(title).content(content).build();
    }

    /**
     * 게시글 즐겨찾기 추가
     */
    @Transactional
    public void favoritePost(Long postId) {
        User user = getCurrentUser();
        Post post = getPostOrThrow(postId);

        if (postFavoriteRepository.existsByUserAndPost(user, post)) {
            throw new CustomException("이미 즐겨찾기한 게시글입니다.");
        }

        PostFavorite favorite = PostFavorite.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        postFavoriteRepository.save(favorite);
    }

    /**
     * 게시글 즐겨찾기 취소
     */
    @Transactional
    public void unfavoritePost(Long postId) {
        User user = getCurrentUser();
        Post post = getPostOrThrow(postId);

        PostFavorite favorite = postFavoriteRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException("즐겨찾기한 게시글이 아닙니다."));

        postFavoriteRepository.delete(favorite);
    }

    /**
     * 즐겨찾기한 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public FavoritePostListResponse getFavoritePosts() {
        User user = getCurrentUser();
        List<PostFavorite> favorites = postFavoriteRepository.findAllByUser(user);

        List<PostSummaryResponse> posts = favorites.stream()
                .map(f -> PostSummaryResponse.from(f.getPost()))
                .collect(Collectors.toList());

        return new FavoritePostListResponse(posts);
    }

    /**
     * 게시글 조회 유틸 메서드
     */
    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }
}
