package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.attachment.request.AttachmentIdRequest;
import com.hamcam.back.dto.community.attachment.request.AttachmentUploadRequest;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostFavorite;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostFavoriteRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.service.community.attachment.AttachmentService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /** ✅ 게시글 생성 */
    public Long createPost(PostCreateRequest request, HttpServletRequest httpRequest) {
        User writer = getSessionUser(httpRequest);

        Post post = Post.builder()
                .writer(writer)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .build();

        post = postRepository.save(post);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            MultipartFile[] fileArray = request.getFiles().toArray(new MultipartFile[0]);
            AttachmentUploadRequest uploadRequest = new AttachmentUploadRequest(post.getId(), fileArray);
            attachmentService.uploadPostFiles(uploadRequest, httpRequest);
        }

        return post.getId();
    }

    /** ✅ 게시글 수정 */
    @Transactional
    public void updatePost(PostUpdateRequest request, HttpServletRequest httpRequest) {
        User sessionUser = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        if (!post.getWriter().getId().equals(sessionUser.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }

        if (request.getDeleteFileIds() != null) {
            request.getDeleteFileIds().forEach(id ->
                    attachmentService.deleteAttachment(new AttachmentIdRequest(id), httpRequest)
            );
        }

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            MultipartFile[] fileArray = request.getFiles().toArray(new MultipartFile[0]);
            AttachmentUploadRequest uploadRequest = new AttachmentUploadRequest(post.getId(), fileArray);
            attachmentService.uploadPostFiles(uploadRequest, httpRequest);
        }
    }

    /** ✅ 게시글 삭제 */
    public void deletePost(PostDeleteRequest request, HttpServletRequest httpRequest) {
        User sessionUser = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        if (!post.getWriter().getId().equals(sessionUser.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        postRepository.delete(post);
    }

    /** ✅ 게시글 상세 조회 */
    public PostResponse getPostDetail(PostDetailRequest request) {
        Post post = getPostOrThrow(request.getPostId());
        post.incrementViewCount();
        postRepository.save(post);
        return PostResponse.from(post);
    }

    /** ✅ 게시글 목록 조회 */
    public PostListResponse getPostList(PostListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAll(pageable);
        return PostListResponse.from(posts);
    }

    /** ✅ 게시글 검색 */
    public PostListResponse searchPosts(PostSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<Post> result = (request.getKeyword() == null || request.getKeyword().isBlank())
                ? postRepository.findAll(pageable)
                : postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                request.getKeyword(), request.getKeyword(), pageable);
        return PostListResponse.from(result);
    }

    /** ✅ 게시글 필터링 */
    public PostListResponse filterPosts(PostFilterRequest request) {
        Sort sort = "popular".equals(request.getSort())
                ? Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("viewCount"))
                : Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(0, 20, sort);

        Page<Post> result = (request.getCategory() == null)
                ? postRepository.searchFilteredPostsWithoutCategory(request.getKeyword(), request.getMinLikes(), pageable)
                : postRepository.searchFilteredPosts(request.getCategory(), request.getKeyword(), request.getMinLikes(), pageable);

        return PostListResponse.from(result);
    }

    /** ✅ 인기 게시글 */
    public PopularPostListResponse getPopularPosts() {
        Page<Post> result = postRepository.findPopularPosts(PageRequest.of(0, 10));
        return PopularPostListResponse.from(result.getContent());
    }

    /** ✅ 랭킹 */
    public RankingResponse getPostRanking() {
        Page<Object[]> ranking = postRepository.getUserPostRanking(PageRequest.of(0, 10));
        return RankingResponse.from(ranking.getContent());
    }

    /** ✅ 자동완성 */
    public PostAutoFillResponse autoFillPost(ProblemReferenceRequest request) {
        String title = "추천 제목: " + request.getProblemTitle();
        String content = "이 문제는 " + request.getCategory() + "에 속하며, 해결 전략은 다음과 같습니다...";
        return PostAutoFillResponse.builder().title(title).content(content).build();
    }

    /** ✅ 즐겨찾기 추가 */
    @Transactional
    public void favoritePost(PostFavoriteRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        if (postFavoriteRepository.existsByUserAndPost(user, post)) {
            throw new CustomException(ErrorCode.DUPLICATE_LIKE);
        }

        postFavoriteRepository.save(PostFavorite.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build());
    }

    /** ✅ 즐겨찾기 제거 */
    @Transactional
    public void unfavoritePost(PostFavoriteRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        PostFavorite favorite = postFavoriteRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT));

        postFavoriteRepository.delete(favorite);
    }

    /** ✅ 즐겨찾기 목록 */
    public FavoritePostListResponse getFavoritePosts(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);

        List<PostFavorite> favorites = postFavoriteRepository.findAllByUser(user);
        List<PostSummaryResponse> posts = favorites.stream()
                .map(f -> PostSummaryResponse.from(f.getPost()))
                .collect(Collectors.toList());

        return new FavoritePostListResponse(posts);
    }

    /** ✅ 조회수 증가 */
    public void increaseViewCount(PostViewRequest request) {
        Post post = getPostOrThrow(request.getPostId());
        post.incrementViewCount();
        postRepository.save(post);
    }

    public StudyInfoListResponse getOngoingStudies() {
        List<StudyInfoDto> studies = List.of(
                StudyInfoDto.builder().name("알고리즘 스터디").color("#e9d8fd")
                        .tag("모집중").tagColor("#a78bfa").info("매주 월/금 20시 | 8명 활동").build(),
                StudyInfoDto.builder().name("프론트엔드 스터디").color("#dbeafe")
                        .tag("모집중").tagColor("#3b82f6").info("매주 토요일 16시 | 10명 활동").build(),
                StudyInfoDto.builder().name("CS 기초 스터디").color("#d1fae5")
                        .tag("모집중").tagColor("#10b981").info("매주 수 14:00 | 10명 활동").build()
        );
        return StudyInfoListResponse.builder().studies(studies).build();
    }

    public TagListResponse getPopularTags() {
        List<String> tags = List.of("알고리즘", "스터디", "React", "Vue", "프로젝트", "취업", "클라우드", "데이터", "python", "javascript", "공유", "팁", "영어");
        return TagListResponse.builder().tags(tags).build();
    }

}
