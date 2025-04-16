package com.hamcam.back.controller.community.post;

import com.hamcam.back.dto.community.post.request.PostCreateRequest;
import com.hamcam.back.dto.community.post.request.PostUpdateRequest;
import com.hamcam.back.dto.community.post.request.ProblemReferenceRequest;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.service.community.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hamcam.back.dto.common.MessageResponse;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성
     */
    @PostMapping
    public ResponseEntity<MessageResponse> createPost(@RequestBody PostCreateRequest request) {
        Long postId = postService.createPost(request);
        return ResponseEntity.ok(new MessageResponse("게시글이 등록되었습니다.", postId));
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<MessageResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        postService.updatePost(postId, request);
        return ResponseEntity.ok(new MessageResponse("게시글이 수정되었습니다."));
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(new MessageResponse("게시글이 삭제되었습니다."));
    }

    /**
     * 게시글 목록 조회 (페이지, 카테고리)
     */
    @GetMapping
    public ResponseEntity<PostListResponse> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(postService.getPostList(page, size, category));
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    /**
     * 게시글 키워드 검색
     */
    @GetMapping("/search")
    public ResponseEntity<PostListResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(postService.searchPosts(keyword, category));
    }

    /**
     * 조건별 게시글 필터링
     */
    @GetMapping("/filter")
    public ResponseEntity<PostListResponse> filterPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(required = false, defaultValue = "0") int minLikes,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(postService.filterPosts(category, sort, minLikes, keyword));
    }

    /**
     * 인기 게시글 목록 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<PopularPostListResponse> getPopularPosts() {
        return ResponseEntity.ok(postService.getPopularPosts());
    }

    /**
     * 게시글 활동 순위 조회
     */
    @GetMapping("/ranking")
    public ResponseEntity<RankingResponse> getPostRanking() {
        return ResponseEntity.ok(postService.getPostRanking());
    }

    /**
     * 실시간 문제풀이방 - 게시글 자동 완성
     */
    @PostMapping("/auto-fill")
    public ResponseEntity<PostAutoFillResponse> autoFillPost(@RequestBody ProblemReferenceRequest request) {
        return ResponseEntity.ok(postService.autoFillPost(request));
    }

    /**
     * 즐겨찾기 등록
     */
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<MessageResponse> favoritePost(@PathVariable Long postId) {
        postService.favoritePost(postId);
        return ResponseEntity.ok(new MessageResponse("즐겨찾기에 추가되었습니다.", true));
    }

    /**
     * 즐겨찾기 해제
     */
    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<MessageResponse> unfavoritePost(@PathVariable Long postId) {
        postService.unfavoritePost(postId);
        return ResponseEntity.ok(new MessageResponse("즐겨찾기에서 제거되었습니다.", false));
    }

    /**
     * 내가 즐겨찾기한 게시글 목록 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<FavoritePostListResponse> getFavoritePosts() {
        return ResponseEntity.ok(postService.getFavoritePosts());
    }
}
