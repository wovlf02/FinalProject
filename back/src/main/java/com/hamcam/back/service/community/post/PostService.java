package com.hamcam.back.service.community.post;

import com.hamcam.back.dto.community.attachment.request.AttachmentIdRequest;
import com.hamcam.back.dto.community.attachment.request.AttachmentUploadRequest;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.dto.community.study.request.SidebarStudyCreateRequest;
import com.hamcam.back.dto.community.study.request.StudyApplicationApprovalRequest;
import com.hamcam.back.dto.community.study.response.StudyInfoDto;
import com.hamcam.back.dto.community.study.response.StudyInfoListResponse;
import com.hamcam.back.dto.community.study.response.UserListResponse;
import com.hamcam.back.dto.community.study.response.UserSimpleDto;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostFavoriteRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.study.SidebarStudyRepository;
import com.hamcam.back.repository.community.study.StudyApplicationRepository;
import com.hamcam.back.repository.community.study.StudyParticipantRepository;
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
    private final SidebarStudyRepository sidebarStudyRepository;
    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyParticipantRepository studyParticipantRepository;

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
        List<SidebarStudy> studyList = sidebarStudyRepository.findAll();

        List<StudyInfoDto> studies = studyList.stream()
                .map(study -> StudyInfoDto.builder()
                        .studyId(study.getId())
                        .name(study.getName())
                        .info(study.getInfo())
                        .schedule(study.getSchedule())
                        .status(study.getStatus())
                        .tag(study.getTag())
                        .color(study.getColor())
                        .tagColor(study.getTagColor())
                        .members(study.getMembers())
                        .build())
                .toList();

        return StudyInfoListResponse.builder()
                .studies(studies)
                .build();
    }


    /** ✅ 사이드바 - 인기 태그 */
    public TagListResponse getPopularTags() {
        List<String> tags = List.of("알고리즘", "자바", "React", "Vue", "CS", "백엔드", "스터디", "정보공유", "고등수학");
        return TagListResponse.builder().tags(tags).build();
    }

    public void createSidebarStudy(SidebarStudyCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SidebarStudy study = SidebarStudy.builder()
                .name(request.getName())
                .info(request.getInfo())
                .schedule(request.getSchedule())
                .status(request.getStatus())
                .tag(request.getTag())
                .color(request.getColor())
                .tagColor(request.getTagColor())
                .members(request.getMembers())
                .creator(user)
                .build();

        sidebarStudyRepository.save(study);

        // 스터디 저장 이후에 참여자로 등록
        StudyParticipant participant = StudyParticipant.builder()
                .study(study)
                .user(user)
                .build();

        studyParticipantRepository.save(participant);

    }

    public StudyInfoDto getSidebarStudyDetail(Long studyId) {
        SidebarStudy study = sidebarStudyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        List<StudyParticipant> participantEntities = studyParticipantRepository.findByStudy(study);

        List<UserSimpleDto> participants = participantEntities.stream()
                .map(p -> UserSimpleDto.builder()
                        .userId(p.getUser().getId())
                        .nickname(p.getUser().getNickname())
                        .build())
                .toList();

        return StudyInfoDto.builder()
                .studyId(study.getId())
                .name(study.getName())
                .info(study.getInfo())
                .schedule(study.getSchedule())
                .status(study.getStatus())
                .tag(study.getTag())
                .color(study.getColor())
                .tagColor(study.getTagColor())
                .members(study.getMembers())
                .currentMembers(participants.size())
                .participants(participants)
                .creatorId(study.getCreator().getId()) // ✅ 생성자 ID 추가
                .build();
    }

    public void applyToStudy(Long studyId, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SidebarStudy study = sidebarStudyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        // ❌ 중복 신청 방지
        if (studyParticipantRepository.existsByStudyAndUser(study, user)) {
            throw new CustomException(ErrorCode.ALREADY_PARTICIPATING);
        }

        if (studyApplicationRepository.existsByStudyAndUserAndStatus(study, user, StudyApplicationStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }

        // ✅ 신청 정보 저장
        StudyApplication application = StudyApplication.builder()
                .study(study)
                .user(user)
                .status(StudyApplicationStatus.PENDING)
                .build();

        studyApplicationRepository.save(application);
    }

    public UserListResponse getStudyApplications(Long studyId, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        SidebarStudy study = sidebarStudyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        // ✅ 생성자만 신청자 목록 조회 가능
        if (!study.getCreator().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        List<StudyApplication> applications = studyApplicationRepository
                .findByStudyAndStatus(study, StudyApplicationStatus.PENDING);
        System.out.println();
        System.out.println(applications);

        List<UserSimpleDto> data = applications.stream()
                .map(app -> UserSimpleDto.builder()
                        .userId(app.getUser().getId())
                        .nickname(app.getUser().getNickname())
                        .build())
                .toList();

        return UserListResponse.builder().data(data).build();
    }

    public void approveStudyApplication(StudyApplicationApprovalRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);

        SidebarStudy study = sidebarStudyRepository.findById(request.getStudyId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        if (!study.getCreator().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        User applicant = userRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        StudyApplication application = studyApplicationRepository
                .findByStudyAndUser(study, applicant)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (request.isApprove()) {
            // ✅ 정원 초과 검사
            int currentMembers = studyParticipantRepository.findByStudy(study).size();
            if (currentMembers >= study.getMembers()) {
                throw new CustomException(ErrorCode.STUDY_FULL);
            }

            // ✅ 참여자로 등록
            StudyParticipant participant = StudyParticipant.builder()
                    .study(study)
                    .user(applicant)
                    .build();
            studyParticipantRepository.save(participant);

            application.setStatus(StudyApplicationStatus.APPROVED);
        } else {
            application.setStatus(StudyApplicationStatus.REJECTED);
        }

        studyApplicationRepository.save(application);
    }



}
