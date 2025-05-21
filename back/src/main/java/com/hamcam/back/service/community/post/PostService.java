package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import com.hamcam.back.entity.community.PostFavorite;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostFavoriteRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.service.community.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    private final UserRepository userRepository;

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    public Long createPost(PostCreateRequest request, MultipartFile[] files, Long userId) {
        User writer = getUser(userId);

        Post post = Post.builder()
                .writer(writer)
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

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, MultipartFile[] files, Long userId) {
        Post post = getPostOrThrow(postId);

        if (!post.getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }

        if (request.getDeleteFileIds() != null) {
            request.getDeleteFileIds().forEach(attachmentService::deleteAttachment);
        }

        if (files != null && files.length > 0) {
            attachmentService.uploadPostFiles(postId, files);
        }
    }

    public void deletePost(Long postId, Long userId) {
        Post post = getPostOrThrow(postId);
        if (!post.getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        postRepository.delete(post);
    }

    public PostResponse getPostDetail(Long postId) {
        Post post = getPostOrThrow(postId);
        post.incrementViewCount();
        postRepository.save(post);
        return PostResponse.from(post);
    }

    public PostListResponse getPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAll(pageable);
        return PostListResponse.from(posts);
    }

    public PostListResponse searchPosts(String keyword, Pageable pageable) {
        Page<Post> result = (keyword == null || keyword.isBlank())
                ? postRepository.findAll(pageable)
                : postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        return PostListResponse.from(result);
    }

    public PostListResponse filterPosts(String sort, int minLikes, String keyword) {
        Sort sortOption = "popular".equals(sort)
                ? Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("viewCount"))
                : Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(0, 20, sortOption);
        Page<Post> result = postRepository.searchFilteredPostsWithoutCategory(keyword, minLikes, pageable);
        return PostListResponse.from(result);
    }

    public PostListResponse filterPostsByCategory(PostCategory category, String keyword, int minLikes, String sort) {
        Sort sortOption = "popular".equals(sort)
                ? Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("viewCount"))
                : Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(0, 20, sortOption);

        Page<Post> result = postRepository.searchFilteredPosts(category, keyword, minLikes, pageable);
        return PostListResponse.from(result);
    }

    public PopularPostListResponse getPopularPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findPopularPosts(pageable);
        return PopularPostListResponse.from(result.getContent());
    }

    public RankingResponse getPostRanking() {
        Page<Object[]> ranking = postRepository.getUserPostRanking(PageRequest.of(0, 10));
        return RankingResponse.from(ranking.getContent());
    }

    public PostAutoFillResponse autoFillPost(ProblemReferenceRequest request) {
        String title = "추천 제목: " + request.getProblemTitle();
        String content = "이 문제는 " + request.getCategory() + "에 속하며, 해결 전략은 다음과 같습니다...";
        return PostAutoFillResponse.builder().title(title).content(content).build();
    }

    @Transactional
    public void favoritePost(Long postId, Long userId) {
        User user = getUser(userId);
        Post post = getPostOrThrow(postId);

        if (postFavoriteRepository.existsByUserAndPost(user, post)) {
            throw new CustomException(ErrorCode.DUPLICATE_LIKE);
        }

        postFavoriteRepository.save(PostFavorite.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void unfavoritePost(Long postId, Long userId) {
        User user = getUser(userId);
        Post post = getPostOrThrow(postId);

        PostFavorite favorite = postFavoriteRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT));
        postFavoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public FavoritePostListResponse getFavoritePosts(Long userId) {
        User user = getUser(userId);
        List<PostFavorite> favorites = postFavoriteRepository.findAllByUser(user);
        List<PostSummaryResponse> posts = favorites.stream()
                .map(f -> PostSummaryResponse.from(f.getPost()))
                .collect(Collectors.toList());
        return new FavoritePostListResponse(posts);
    }

    public void increaseViewCount(Long postId) {
        Post post = getPostOrThrow(postId);
        post.incrementViewCount();
        postRepository.save(post);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}