package com.hamcam.back.controller.community.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.entity.community.PostCategory;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.service.community.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** ✅ 게시글 생성 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createPost(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        try {
            PostCreateRequest request = objectMapper.readValue(requestJson, PostCreateRequest.class);
            Long postId = postService.createPost(request.getUserId(), request, files);
            return ResponseEntity.ok(MessageResponse.of("게시글이 등록되었습니다.", postId));
        } catch (JsonProcessingException e) {
            throw new CustomException("게시글 데이터 파싱 오류: " + e.getOriginalMessage());
        }
    }

    /** ✅ 게시글 수정 */
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updatePost(
            @PathVariable Long postId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        try {
            PostUpdateRequest request = objectMapper.readValue(requestJson, PostUpdateRequest.class);
            postService.updatePost(postId, request.getUserId(), request, files);
            return ResponseEntity.ok(MessageResponse.of("게시글이 수정되었습니다."));
        } catch (JsonProcessingException e) {
            throw new CustomException("게시글 데이터 파싱 오류: " + e.getOriginalMessage());
        }
    }

    /** ✅ 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(
            @PathVariable Long postId,
            @RequestBody PostUserRequest request
    ) {
        postService.deletePost(postId, request.getUserId());
        return ResponseEntity.ok(MessageResponse.of("게시글이 삭제되었습니다."));
    }

    /** ✅ 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<PostListResponse> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getPostList(page, size));
    }

    /** ✅ 게시글 상세 조회 */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    /** ✅ 게시글 검색 */
    @GetMapping("/search")
    public ResponseEntity<PostListResponse> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.searchPosts(keyword, pageable));
    }

    /** ✅ 게시글 필터링 */
    @GetMapping("/filter")
    public ResponseEntity<PostListResponse> filterPosts(
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int minLikes,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PostCategory category
    ) {
        return (category != null)
                ? ResponseEntity.ok(postService.filterPostsByCategory(category, keyword, minLikes, sort))
                : ResponseEntity.ok(postService.filterPosts(sort, minLikes, keyword));
    }

    /** ✅ 인기 게시글 조회 */
    @GetMapping("/popular")
    public ResponseEntity<PopularPostListResponse> getPopularPosts() {
        return ResponseEntity.ok(postService.getPopularPosts());
    }

    /** ✅ 게시글 작성자 랭킹 */
    @GetMapping("/ranking")
    public ResponseEntity<RankingResponse> getPostRanking() {
        return ResponseEntity.ok(postService.getPostRanking());
    }

    /** ✅ 게시글 자동완성 */
    @PostMapping("/auto-fill")
    public ResponseEntity<PostAutoFillResponse> autoFillPost(@RequestBody ProblemReferenceRequest request) {
        return ResponseEntity.ok(postService.autoFillPost(request));
    }

    /** ✅ 즐겨찾기 추가 */
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<MessageResponse> favoritePost(
            @PathVariable Long postId,
            @RequestBody PostUserRequest request
    ) {
        postService.favoritePost(postId, request.getUserId());
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에 추가되었습니다.", true));
    }

    /** ✅ 즐겨찾기 제거 */
    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<MessageResponse> unfavoritePost(
            @PathVariable Long postId,
            @RequestBody PostUserRequest request
    ) {
        postService.unfavoritePost(postId, request.getUserId());
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에서 제거되었습니다.", false));
    }

    /** ✅ 즐겨찾기 목록 조회 */
    @PostMapping("/favorites")
    public ResponseEntity<FavoritePostListResponse> getFavoritePosts(@RequestBody PostUserRequest request) {
        return ResponseEntity.ok(postService.getFavoritePosts(request.getUserId()));
    }

    /** ✅ 게시글 조회수 증가 */
    @PostMapping("/{postId}/view")
    public ResponseEntity<MessageResponse> increaseViewCount(@PathVariable Long postId) {
        postService.increaseViewCount(postId);
        return ResponseEntity.ok(MessageResponse.of("조회수가 증가되었습니다."));
    }
}
