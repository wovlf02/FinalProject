package com.hamcam.back.controller.community.post;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.study.request.StudyApplicationApprovalRequest;
import com.hamcam.back.dto.community.study.request.StudyApplyRequest;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.dto.community.study.request.SidebarStudyCreateRequest;
import com.hamcam.back.dto.community.study.response.StudyInfoDto;
import com.hamcam.back.dto.community.study.response.StudyInfoListResponse;
import com.hamcam.back.dto.community.study.response.UserListResponse;
import com.hamcam.back.service.community.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** ✅ 게시글 생성 (multipart/form-data) */
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("category") String category,
            @RequestPart(value = "tag", required = false) String tag,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest httpRequest
    ) {
        PostCreateRequest request = PostCreateRequest.builder()
                .title(title)
                .content(content)
                .category(category)
                .tag(tag)
                .build();

        Long postId = postService.createPost(request, file, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 등록되었습니다.", postId));
    }

    /** ✅ 게시글 수정 (multipart/form-data) */
    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> updatePost(
            @ModelAttribute PostUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.updatePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 수정되었습니다."));
    }

    /** ✅ 게시글 삭제 */
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deletePost(
            @RequestBody PostDeleteRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.deletePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 삭제되었습니다."));
    }

    /** ✅ 게시글 목록 조회 (카테고리 + 검색 + 페이징 통합) */
    @PostMapping("/list")
    public ResponseEntity<PostListResponse> getPostList(@RequestBody PostListRequest request) {
        return ResponseEntity.ok(postService.getPostList(request));
    }

    /** ✅ 게시글 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<PostResponse> getPostDetail(@RequestBody PostDetailRequest request) {
        return ResponseEntity.ok(postService.getPostDetail(request));
    }

    /** ✅ 인기 게시글 조회 */
    @GetMapping("/popular")
    public ResponseEntity<PopularPostListResponse> getPopularPosts() {
        return ResponseEntity.ok(postService.getPopularPosts());
    }

    /** ✅ 문제 기반 자동 완성 */
    @PostMapping("/auto-fill")
    public ResponseEntity<ProblemReferenceResponse> autoFillPost(@RequestBody ProblemReferenceRequest request) {
        return ResponseEntity.ok(postService.autoFillPost(request));
    }

    /** ✅ 게시글 즐겨찾기 추가 */
    @PostMapping("/favorite/add")
    public ResponseEntity<MessageResponse> favoritePost(
            @RequestBody PostFavoriteRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.favoritePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에 추가되었습니다.", true));
    }

    /** ✅ 게시글 즐겨찾기 제거 */
    @PostMapping("/favorite/remove")
    public ResponseEntity<MessageResponse> unfavoritePost(
            @RequestBody PostFavoriteRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.unfavoritePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에서 제거되었습니다.", false));
    }

    /** ✅ 즐겨찾기 게시글 목록 조회 */
    @GetMapping("/favorites")
    public ResponseEntity<FavoritePostListResponse> getFavoritePosts(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(postService.getFavoritePosts(httpRequest));
    }

    /** ✅ 게시글 조회수 증가 */
    @PatchMapping("/view")
    public ResponseEntity<MessageResponse> increaseViewCount(@RequestBody PostViewRequest request) {
        postService.increaseViewCount(request);
        return ResponseEntity.ok(MessageResponse.of("조회수가 증가되었습니다."));
    }

    /** ✅ 사이드바 - 진행 중인 스터디 리스트 */
    @GetMapping("/sidebar/studies")
    public ResponseEntity<StudyInfoListResponse> getSidebarStudyList() {
        return ResponseEntity.ok(postService.getOngoingStudies());
    }

    /** ✅ 사이드바 - 인기 태그 리스트 */
    @GetMapping("/sidebar/tags")
    public ResponseEntity<TagListResponse> getPopularTags() {
        return ResponseEntity.ok(postService.getPopularTags());
    }

    /** ✅ 사이드바 - 진행 중인 스터디 생성 */
    @PostMapping("/sidebar/studies/create")
    public ResponseEntity<MessageResponse> createSidebarStudy(
            @RequestBody SidebarStudyCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.createSidebarStudy(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("스터디가 생성되었습니다."));
    }

    /** ✅ 사이드바 - 스터디 상세 조회 */
    @GetMapping("/sidebar/studies/{studyId}")
    public ResponseEntity<StudyInfoDto> getSidebarStudyDetail(@PathVariable Long studyId) {
        return ResponseEntity.ok(postService.getSidebarStudyDetail(studyId));
    }

    /** ✅ 스터디 참여 신청 */
    @PostMapping("/sidebar/studies/apply")
    public ResponseEntity<MessageResponse> applyToStudy(
            @RequestBody StudyApplyRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.applyToStudy(request.getStudyId(), httpRequest);
        return ResponseEntity.ok(MessageResponse.of("스터디 참여 신청이 완료되었습니다."));
    }

    @GetMapping("/sidebar/studies/{studyId}/applications")
    public ResponseEntity<UserListResponse> getStudyApplications(
            @PathVariable Long studyId,
            HttpServletRequest httpRequest
    ) {
        System.out.println(studyId);
        return ResponseEntity.ok(postService.getStudyApplications(studyId, httpRequest));
    }


    @PostMapping("/sidebar/studies/approve")
    public ResponseEntity<MessageResponse> approveStudyApplication(
            @RequestBody StudyApplicationApprovalRequest request,
            HttpServletRequest httpRequest
    ) {
        postService.approveStudyApplication(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of(
                request.isApprove() ? "신청이 수락되었습니다." : "신청이 거절되었습니다."
        ));
    }

}
