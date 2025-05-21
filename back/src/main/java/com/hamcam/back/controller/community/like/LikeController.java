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
 * 게시글, 댓글, 대댓글 각각의 좋아요 처리 및 상태 조회 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/community/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // ===================== 게시글 =====================

    /** 게시글 좋아요 토글 */
    @PostMapping("/posts/{postId}/toggle")
    public ResponseEntity<MessageResponse> togglePostLike(
            @PathVariable Long postId,
            @RequestParam("userId") Long userId
    ) {
        boolean liked = likeService.togglePostLike(postId, userId);
        String message = liked ? "게시글에 좋아요를 눌렀습니다." : "게시글 좋아요를 취소했습니다.";
        return ResponseEntity.ok(MessageResponse.of(message, liked));
    }

    /** 게시글 좋아요 수 조회 */
    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<LikeCountResponse> getPostLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikeCount(postId));
    }

    /** 게시글 좋아요 여부 조회 */
    @GetMapping("/posts/{postId}/check")
    public ResponseEntity<LikeStatusResponse> checkPostLike(
            @PathVariable Long postId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(likeService.hasLikedPost(postId, userId));
    }

    // ===================== 댓글 =====================

    /** 댓글 좋아요 토글 */
    @PostMapping("/comments/{commentId}/toggle")
    public ResponseEntity<MessageResponse> toggleCommentLike(
            @PathVariable Long commentId,
            @RequestParam("userId") Long userId
    ) {
        boolean liked = likeService.toggleCommentLike(commentId, userId);
        String message = liked ? "댓글에 좋아요를 눌렀습니다." : "댓글 좋아요를 취소했습니다.";
        return ResponseEntity.ok(MessageResponse.of(message, liked));
    }

    /** 댓글 좋아요 수 조회 */
    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<LikeCountResponse> getCommentLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.getCommentLikeCount(commentId));
    }

    /** 댓글 좋아요 여부 조회 */
    @GetMapping("/comments/{commentId}/check")
    public ResponseEntity<LikeStatusResponse> checkCommentLike(
            @PathVariable Long commentId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(likeService.hasLikedComment(commentId, userId));
    }

    // ===================== 대댓글 =====================

    /** 대댓글 좋아요 토글 */
    @PostMapping("/replies/{replyId}/toggle")
    public ResponseEntity<MessageResponse> toggleReplyLike(
            @PathVariable Long replyId,
            @RequestParam("userId") Long userId
    ) {
        boolean liked = likeService.toggleReplyLike(replyId, userId);
        String message = liked ? "대댓글에 좋아요를 눌렀습니다." : "대댓글 좋아요를 취소했습니다.";
        return ResponseEntity.ok(MessageResponse.of(message, liked));
    }

    /** 대댓글 좋아요 수 조회 */
    @GetMapping("/replies/{replyId}/count")
    public ResponseEntity<LikeCountResponse> getReplyLikeCount(@PathVariable Long replyId) {
        return ResponseEntity.ok(likeService.getReplyLikeCount(replyId));
    }

    /** 대댓글 좋아요 여부 조회 */
    @GetMapping("/replies/{replyId}/check")
    public ResponseEntity<LikeStatusResponse> checkReplyLike(
            @PathVariable Long replyId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(likeService.hasLikedReply(replyId, userId));
    }
}
