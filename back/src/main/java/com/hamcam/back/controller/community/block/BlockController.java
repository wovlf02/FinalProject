package com.hamcam.back.controller.community.block;

import com.hamcam.back.dto.community.block.response.BlockedPostListResponse;
import com.hamcam.back.dto.community.block.response.BlockedCommentListResponse;
import com.hamcam.back.dto.community.block.response.BlockedReplyListResponse;
import com.hamcam.back.service.community.block.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hamcam.back.dto.common.MessageResponse;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    /**
     * 게시글 차단
     */
    @PostMapping("/posts/{postId}/block")
    public ResponseEntity<MessageResponse> blockPost(@PathVariable Long postId) {
        blockService.blockPost(postId);
        return ResponseEntity.ok(new MessageResponse("해당 게시글이 차단되었습니다."));
    }

    /**
     * 게시글 차단 해제
     */
    @DeleteMapping("/posts/{postId}/block")
    public ResponseEntity<MessageResponse> unblockPost(@PathVariable Long postId) {
        blockService.unblockPost(postId);
        return ResponseEntity.ok(new MessageResponse("차단이 해제되었습니다."));
    }

    /**
     * 차단된 게시글 목록 조회
     */
    @GetMapping("/posts/blocked")
    public ResponseEntity<BlockedPostListResponse> getBlockedPosts() {
        return ResponseEntity.ok(blockService.getBlockedPosts());
    }

    /**
     * 댓글 차단
     */
    @PostMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> blockComment(@PathVariable Long commentId) {
        blockService.blockComment(commentId);
        return ResponseEntity.ok(new MessageResponse("해당 댓글이 차단되었습니다."));
    }

    /**
     * 댓글 차단 해제
     */
    @DeleteMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> unblockComment(@PathVariable Long commentId) {
        blockService.unblockComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 차단이 해제되었습니다."));
    }

    /**
     * 차단된 댓글 목록 조회
     */
    @GetMapping("/comments/blocked")
    public ResponseEntity<BlockedCommentListResponse> getBlockedComments() {
        return ResponseEntity.ok(blockService.getBlockedComments());
    }

    /**
     * 대댓글 차단
     */
    @PostMapping("/replies/{replyId}/block")
    public ResponseEntity<MessageResponse> blockReply(@PathVariable Long replyId) {
        blockService.blockReply(replyId);
        return ResponseEntity.ok(new MessageResponse("해당 대댓글이 차단되었습니다."));
    }

    /**
     * 대댓글 차단 해제
     */
    @DeleteMapping("/replies/{replyId}/block")
    public ResponseEntity<MessageResponse> unblockReply(@PathVariable Long replyId) {
        blockService.unblockReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글 차단이 해제되었습니다."));
    }

    /**
     * 차단된 대댓글 목록 조회
     */
    @GetMapping("/replies/blocked")
    public ResponseEntity<BlockedReplyListResponse> getBlockedReplies() {
        return ResponseEntity.ok(blockService.getBlockedReplies());
    }
}
