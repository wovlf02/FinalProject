package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.attachment.request.AttachmentIdRequest;
import com.hamcam.back.dto.community.attachment.request.AttachmentUploadRequest;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
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
    public Long createPost(PostCreateRequest request, MultipartFile file, HttpServletRequest httpRequest) {
        User writer = getSessionUser(httpRequest);

        PostCategory category;
        try {
            category = PostCategory.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_POST_CATEGORY);
        }

        Post post = Post.builder()
                .writer(writer)
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .tag(request.getTag())
                .createdAt(LocalDateTime.now())
                .build();

        post = postRepository.save(post);

        if (file != null && !file.isEmpty()) {
            MultipartFile[] fileArray = new MultipartFile[]{file};
            attachmentService.uploadPostFiles(
                    new AttachmentUploadRequest(post.getId(), fileArray),
                    httpRequest
            );
        }

        return post.getId();
    }

    /** ✅ 게시글 수정 */
    @Transactional
    public void updatePost(PostUpdateRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        if (!post.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                PostCategory category = PostCategory.valueOf(request.getCategory().toUpperCase());
                post.setCategory(category);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_INPUT);
            }
        }

        if (request.getTag() != null) {
            post.setTag(request.getTag());
        }

        if (request.getDeleteFileIds() != null) {
            for (Long fileId : request.getDeleteFileIds()) {
                attachmentService.deleteAttachment(new AttachmentIdRequest(fileId), httpRequest);
            }
        }

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            MultipartFile[] fileArray = request.getFiles().toArray(new MultipartFile[0]);
            attachmentService.uploadPostFiles(new AttachmentUploadRequest(post.getId(), fileArray), httpRequest);
        }
    }

    /** ✅ 게시글 삭제 */
    public void deletePost(PostDeleteRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPostOrThrow(request.getPostId());

        if (!post.getWriter().getId().equals(user.getId())) {
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

    /** ✅ 게시글 목록 조회 (카테고리 + 검색 + 페이징 통합) */
    public PostListResponse getPostList(PostListRequest request) {
        int page = request.getPageOrDefault() - 1;
        int size = request.getSizeOrDefault();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String searchType = request.getSearchTypeOrNull();
        String keyword = request.getKeywordOrNull();
        String categoryStr = request.getCategoryOrNull();

        PostCategory category = null;
        if (categoryStr != null) {
            try {
                category = PostCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_POST_CATEGORY);
            }
        }

        Page<Post> result;

        if (keyword == null || keyword.isBlank()) {
            result = (category == null)
                    ? postRepository.findAll(pageable)
                    : postRepository.findAllByCategory(category, pageable);
        } else {
            switch (searchType) {
                case "title":
                    result = (category == null)
                            ? postRepository.findByTitleContainingIgnoreCase(keyword, pageable)
                            : postRepository.findByCategoryAndTitleContainingIgnoreCase(category, keyword, pageable);
                    break;
                case "content":
                    result = (category == null)
                            ? postRepository.findByContentContainingIgnoreCase(keyword, pageable)
                            : postRepository.findByCategoryAndContentContainingIgnoreCase(category, keyword, pageable);
                    break;
                case "author":
                    result = (category == null)
                            ? postRepository.findByWriter_NicknameContainingIgnoreCase(keyword, pageable)
                            : postRepository.findByCategoryAndWriter_NicknameContainingIgnoreCase(category, keyword, pageable);
                    break;
                case "title_content":
                default:
                    result = (category == null)
                            ? postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable)
                            : postRepository.searchByCategoryAndKeyword(category, keyword, pageable);
                    break;
            }
        }

        return PostListResponse.from(
                result,
                category != null ? category.name() : null,
                keyword
        );
    }



    /** ✅ 인기 게시글 조회 */
    public PopularPostListResponse getPopularPosts() {
        Page<Post> posts = postRepository.findPopularPosts(PageRequest.of(0, 10));
        return PopularPostListResponse.from(posts.getContent());
    }

    /** ✅ 자동완성 */
    public ProblemReferenceResponse autoFillPost(ProblemReferenceRequest request) {
        PostCategory category;
        try {
            category = PostCategory.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return ProblemReferenceResponse.builder()
                .title("문제: " + request.getProblemTitle())
                .content("이 문제는 " + category.getLabel() + " 유형에 속하며 해결 전략은 다음과 같습니다...")
                .category(category)
                .build();
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

    /** ✅ 즐겨찾기 목록 조회 */
    public FavoritePostListResponse getFavoritePosts(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<PostFavorite> favorites = postFavoriteRepository.findAllByUser(user);
        List<PostSummaryResponse> posts = favorites.stream()
                .map(fav -> PostSummaryResponse.from(fav.getPost()))
                .collect(Collectors.toList());
        return new FavoritePostListResponse(posts);
    }

    /** ✅ 조회수 증가 */
    public void increaseViewCount(PostViewRequest request) {
        Post post = getPostOrThrow(request.getPostId());
        post.incrementViewCount();
        postRepository.save(post);
    }

    /** ✅ 사이드바 - 진행 중인 스터디 */
    public StudyInfoListResponse getOngoingStudies() {
        List<StudyInfoDto> studies = List.of(
                StudyInfoDto.builder().name("알고리즘 스터디").color("#e9d8fd").tag("모집중").tagColor("#a78bfa").info("매주 월/금 20시 | 8명 활동").build(),
                StudyInfoDto.builder().name("프론트엔드 스터디").color("#dbeafe").tag("모집중").tagColor("#3b82f6").info("매주 토요일 16시 | 10명 활동").build(),
                StudyInfoDto.builder().name("CS 기초 스터디").color("#d1fae5").tag("모집중").tagColor("#10b981").info("매주 수 14:00 | 10명 활동").build()
        );
        return StudyInfoListResponse.builder().studies(studies).build();
    }

    /** ✅ 사이드바 - 인기 태그 */
    public TagListResponse getPopularTags() {
        List<String> tags = List.of("알고리즘", "자바", "React", "Vue", "CS", "백엔드", "스터디", "정보공유", "고등수학");
        return TagListResponse.builder().tags(tags).build();
    }
}
