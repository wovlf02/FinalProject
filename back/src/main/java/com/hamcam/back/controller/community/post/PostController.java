package com.hamcam.back.controller.community.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.post.request.*;
import com.hamcam.back.dto.community.post.response.*;
import com.hamcam.back.service.community.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** ✅ 게시글 생성 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createPost(
            @RequestPart("request") String requestJson
    ) {
        try {
            PostCreateRequest request = objectMapper.readValue(requestJson, PostCreateRequest.class);
            Long postId = postService.createPost(request);
            return ResponseEntity.ok(MessageResponse.of("게시글이 등록되었습니다.", postId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("게시글 데이터 파싱 오류: " + e.getOriginalMessage());
        }
    }

    /** ✅ 게시글 수정 */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updatePost(
            @RequestPart("request") String requestJson
    ) {
        try {
            PostUpdateRequest request = objectMapper.readValue(requestJson, PostUpdateRequest.class);
            postService.updatePost(request);
            return ResponseEntity.ok(MessageResponse.of("게시글이 수정되었습니다."));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("게시글 데이터 파싱 오류: " + e.getOriginalMessage());
        }
    }

    /** ✅ 게시글 삭제 */
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deletePost(@RequestBody PostDeleteRequest request) {
        postService.deletePost(request);
        return ResponseEntity.ok(MessageResponse.of("게시글이 삭제되었습니다."));
    }

    /** ✅ 게시글 목록 조회 */
    @PostMapping("/list")
    public ResponseEntity<PostListResponse> getPostList(@RequestBody PostListRequest request) {
        return ResponseEntity.ok(postService.getPostList(request));
    }

    /** ✅ 게시글 상세 조회 */
    @PostMapping("/detail")
    public ResponseEntity<PostResponse> getPostDetail(@RequestBody PostDetailRequest request) {
        return ResponseEntity.ok(postService.getPostDetail(request));
    }

    /** ✅ 게시글 검색 */
    @PostMapping("/search")
    public ResponseEntity<PostListResponse> searchPosts(@RequestBody PostSearchRequest request) {
        return ResponseEntity.ok(postService.searchPosts(request));
    }

    /** ✅ 게시글 필터링 */
    @PostMapping("/filter")
    public ResponseEntity<PostListResponse> filterPosts(@RequestBody PostFilterRequest request) {
        return ResponseEntity.ok(postService.filterPosts(request));
    }

    /** ✅ 인기 게시글 조회 */
    @PostMapping("/popular")
    public ResponseEntity<PopularPostListResponse> getPopularPosts() {
        return ResponseEntity.ok(postService.getPopularPosts());
    }

    /** ✅ 게시글 작성자 랭킹 */
    @PostMapping("/ranking")
    public ResponseEntity<RankingResponse> getPostRanking() {
        return ResponseEntity.ok(postService.getPostRanking());
    }

    /** ✅ 게시글 자동완성 */
    @PostMapping("/auto-fill")
    public ResponseEntity<PostAutoFillResponse> autoFillPost(@RequestBody ProblemReferenceRequest request) {
        return ResponseEntity.ok(postService.autoFillPost(request));
    }

    /** ✅ 즐겨찾기 추가 */
    @PostMapping("/favorite/add")
    public ResponseEntity<MessageResponse> favoritePost(@RequestBody PostFavoriteRequest request) {
        postService.favoritePost(request);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에 추가되었습니다.", true));
    }

    /** ✅ 즐겨찾기 제거 */
    @PostMapping("/favorite/remove")
    public ResponseEntity<MessageResponse> unfavoritePost(@RequestBody PostFavoriteRequest request) {
        postService.unfavoritePost(request);
        return ResponseEntity.ok(MessageResponse.of("즐겨찾기에서 제거되었습니다.", false));
    }

    /** ✅ 즐겨찾기 목록 조회 */
    @PostMapping("/favorites")
    public ResponseEntity<FavoritePostListResponse> getFavoritePosts(@RequestBody PostUserRequest request) {
        return ResponseEntity.ok(postService.getFavoritePosts(request));
    }

    /** ✅ 게시글 조회수 증가 */
    @PostMapping("/view")
    public ResponseEntity<MessageResponse> increaseViewCount(@RequestBody PostViewRequest request) {
        postService.increaseViewCount(request);
        return ResponseEntity.ok(MessageResponse.of("조회수가 증가되었습니다."));
    }
}
