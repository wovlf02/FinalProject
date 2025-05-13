package com.hamcam.back.controller.community.like;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.like.response.LikeCountResponse;
import com.hamcam.back.dto.community.like.response.LikeStatusResponse;
import com.hamcam.back.service.community.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 좋아요(Like) 관련 REST 컨트롤러
 * <p>
 * 게시글, 댓글, 대댓글 각각의 좋아요 처리 및 상태 조회 API를 제공합니다.
 * URI 충돌 방지를 위해 리소스별로 경로를 세분화합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/community/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // ===================== 게시글 =====================

    /** 게시글 좋아요 추가 */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<MessageResponse> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok(new MessageResponse("게시글에 좋아요를 눌렀습니다."));
    }

    /** 게시글 좋아요 취소 */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<MessageResponse> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.ok(new MessageResponse("게시글 좋아요를 취소했습니다."));
    }

    /** 게시글 좋아요 수 조회 */
    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<LikeCountResponse> getPostLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikeCount(postId));
    }

    /** 게시글 좋아요 여부 조회 */
    @GetMapping("/posts/{postId}/check")
    public ResponseEntity<LikeStatusResponse> checkPostLike(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.hasLikedPost(postId));
    }

    // ===================== 댓글 =====================

    /** 댓글 좋아요 추가 */
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> likeComment(@PathVariable Long commentId) {
        likeService.likeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글에 좋아요를 눌렀습니다."));
    }

    /** 댓글 좋아요 취소 */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> unlikeComment(@PathVariable Long commentId) {
        likeService.unlikeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 좋아요를 취소했습니다."));
    }

    /** 댓글 좋아요 수 조회 */
    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<LikeCountResponse> getCommentLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.getCommentLikeCount(commentId));
    }

    /** 댓글 좋아요 여부 조회 */
    @GetMapping("/comments/{commentId}/check")
    public ResponseEntity<LikeStatusResponse> checkCommentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.hasLikedComment(commentId));
    }

    // ===================== 대댓글 =====================

    /** 대댓글 좋아요 추가 */
    @PostMapping("/replies/{replyId}")
    public ResponseEntity<MessageResponse> likeReply(@PathVariable Long replyId) {
        likeService.likeReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글에 좋아요를 눌렀습니다."));
    }

    /** 대댓글 좋아요 취소 */
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<MessageResponse> unlikeReply(@PathVariable Long replyId) {
        likeService.unlikeReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글 좋아요를 취소했습니다."));
    }

    /** 대댓글 좋아요 수 조회 */
    @GetMapping("/replies/{replyId}/count")
    public ResponseEntity<LikeCountResponse> getReplyLikeCount(@PathVariable Long replyId) {
        return ResponseEntity.ok(likeService.getReplyLikeCount(replyId));
    }

    /** 대댓글 좋아요 여부 조회 */
    @GetMapping("/replies/{replyId}/check")
    public ResponseEntity<LikeStatusResponse> checkReplyLike(@PathVariable Long replyId) {
        return ResponseEntity.ok(likeService.hasLikedReply(replyId));
    }
}
