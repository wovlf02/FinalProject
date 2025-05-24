package com.hamcam.back.controller.community.post;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.service.community.post.PostService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createPost(@RequestBody PostCreateRequest request, HttpServletRequest httpRequest) {
        Long postId = postService.createPost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 등록되었습니다.", postId));
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updatePost(@RequestBody PostUpdateRequest request, HttpServletRequest httpRequest) {
        postService.updatePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 수정되었습니다."));
    }

    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deletePost(@RequestBody PostDeleteRequest request, HttpServletRequest httpRequest) {
        postService.deletePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 삭제되었습니다."));
    }

    @PostMapping("/list")
    public ResponseEntity<PostListResponse> getPostList(@RequestBody PostListRequest request) {
        return ResponseEntity.ok(postService.getPostList(request));
    }

    @PostMapping("/detail")
    public ResponseEntity<PostResponse> getPostDetail(@RequestBody PostDetailRequest request) {
        return ResponseEntity.ok(postService.getPostDetail(request));
    }

    @PostMapping("/search")
    public ResponseEntity<PostListResponse> searchPosts(@RequestBody PostSearchRequest request) {
        return ResponseEntity.ok(postService.searchPosts(request));
    }

    @PostMapping("/filter")
    public ResponseEntity<PostListResponse> filterPosts(@RequestBody PostFilterRequest request) {
        return ResponseEntity.ok(postService.filterPosts(request));
    }

    @GetMapping("/popular") // ✅ GET으로 수정
    public ResponseEntity<PopularPostListResponse> getPopularPosts(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(postService.getPopularPosts());
    }

    @GetMapping("/ranking") // ✅ GET으로 수정
    public ResponseEntity<RankingResponse> getPostRanking(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(postService.getPostRanking());
    }

    @PostMapping("/auto-fill")
    public ResponseEntity<PostAutoFillResponse> autoFillPost(@RequestBody ProblemReferenceRequest request) {
        return ResponseEntity.ok(postService.autoFillPost(request));
    }

    @PostMapping("/favorite/add")
    public ResponseEntity<MessageResponse> favoritePost(@RequestBody PostFavoriteRequest request, HttpServletRequest httpRequest) {
        postService.favoritePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에 추가되었습니다.", true));
    }

    @PostMapping("/favorite/remove")
    public ResponseEntity<MessageResponse> unfavoritePost(@RequestBody PostFavoriteRequest request, HttpServletRequest httpRequest) {
        postService.unfavoritePost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에서 제거되었습니다.", false));
    }

    @GetMapping("/favorites") // ✅ GET으로 수정
    public ResponseEntity<FavoritePostListResponse> getFavoritePosts(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(postService.getFavoritePosts(httpRequest));
    }

    @PatchMapping("/view") // ✅ POST → PATCH로 변경
    public ResponseEntity<MessageResponse> increaseViewCount(@RequestBody PostViewRequest request) {
        postService.increaseViewCount(request);
        return ResponseEntity.ok(MessageResponse.of("조회수가 증가되었습니다."));
    }

    @GetMapping("/sidebar/studies")
    public ResponseEntity<StudyInfoListResponse> getSidebarStudyList() {
        return ResponseEntity.ok(postService.getOngoingStudies());
    }

    @GetMapping("/sidebar/tags")
    public ResponseEntity<TagListResponse> getPopularTags() {
        return ResponseEntity.ok(postService.getPopularTags());
    }

}
