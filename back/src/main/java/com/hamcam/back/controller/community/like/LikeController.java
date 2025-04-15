package com.hamcam.back.controller.community.like;

import com.hamcam.back.dto.community.like.response.LikeCountResponse;
import com.hamcam.back.dto.community.like.response.LikeStatusResponse;
import com.hamcam.back.service.community.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hamcam.back.dto.common.MessageResponse;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 게시글 좋아요 추가
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<MessageResponse> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok(new MessageResponse("게시글에 좋아요를 눌렀습니다."));
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<MessageResponse> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.ok(new MessageResponse("게시글 좋아요가 취소되었습니다."));
    }

    /**
     * 게시글 좋아요 수 조회
     */
    @GetMapping("/posts/{postId}/likes/count")
    public ResponseEntity<LikeCountResponse> getPostLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikeCount(postId));
    }

    /**
     * 게시글 좋아요 여부 조회
     */
    @GetMapping("/posts/{postId}/likes/check")
    public ResponseEntity<LikeStatusResponse> checkPostLike(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.hasLikedPost(postId));
    }

    /**
     * 댓글 좋아요 추가
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> likeComment(@PathVariable Long commentId) {
        likeService.likeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글에 좋아요를 눌렀습니다."));
    }

    /**
     * 댓글 좋아요 취소
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> unlikeComment(@PathVariable Long commentId) {
        likeService.unlikeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 좋아요가 취소되었습니다."));
    }

    /**
     * 댓글 좋아요 수 조회
     */
    @GetMapping("/comments/{commentId}/likes/count")
    public ResponseEntity<LikeCountResponse> getCommentLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.getCommentLikeCount(commentId));
    }

    /**
     * 댓글 좋아요 여부 조회
     */
    @GetMapping("/comments/{commentId}/likes/check")
    public ResponseEntity<LikeStatusResponse> checkCommentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.hasLikedComment(commentId));
    }

    /**
     * 대댓글 좋아요 추가
     */
    @PostMapping("/replies/{replyId}/like")
    public ResponseEntity<MessageResponse> likeReply(@PathVariable Long replyId) {
        likeService.likeReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글에 좋아요를 눌렀습니다."));
    }

    /**
     * 대댓글 좋아요 취소
     */
    @DeleteMapping("/replies/{replyId}/like")
    public ResponseEntity<MessageResponse> unlikeReply(@PathVariable Long replyId) {
        likeService.unlikeReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글 좋아요가 취소되었습니다."));
    }

    /**
     * 대댓글 좋아요 수 조회
     */
    @GetMapping("/replies/{replyId}/likes/count")
    public ResponseEntity<LikeCountResponse> getReplyLikeCount(@PathVariable Long replyId) {
        return ResponseEntity.ok(likeService.getReplyLikeCount(replyId));
    }

    /**
     * 대댓글 좋아요 여부 조회
     */
    @GetMapping("/replies/{replyId}/likes/check")
    public ResponseEntity<LikeStatusResponse> checkReplyLike(@PathVariable Long replyId) {
        return ResponseEntity.ok(likeService.hasLikedReply(replyId));
    }
}
