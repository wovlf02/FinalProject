package com.hamcam.back.controller.community.block;

import com.hamcam.back.dto.community.block.response.BlockedPostListResponse;
import com.hamcam.back.dto.community.block.response.BlockedCommentListResponse;
import com.hamcam.back.dto.community.block.response.BlockedReplyListResponse;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.service.community.block.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [BlockController]
 * 커뮤니티 내 게시글/댓글/대댓글 차단 및 해제 기능을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    // ========== 📌 게시글 차단 ==========

    @PostMapping("/posts/{postId}/block")
    public ResponseEntity<MessageResponse> blockPost(@PathVariable Long postId) {
        blockService.blockPost(postId);
        return message("🛑 게시글을 차단했습니다.");
    }

    @DeleteMapping("/posts/{postId}/block")
    public ResponseEntity<MessageResponse> unblockPost(@PathVariable Long postId) {
        blockService.unblockPost(postId);
        return message("🔓 게시글 차단을 해제했습니다.");
    }

    @GetMapping("/posts/blocked")
    public ResponseEntity<BlockedPostListResponse> getBlockedPosts() {
        return ResponseEntity.ok(blockService.getBlockedPosts());
    }

    // ========== 💬 댓글 차단 ==========

    @PostMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> blockComment(@PathVariable Long commentId) {
        blockService.blockComment(commentId);
        return message("🛑 댓글을 차단했습니다.");
    }

    @DeleteMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> unblockComment(@PathVariable Long commentId) {
        blockService.unblockComment(commentId);
        return message("🔓 댓글 차단을 해제했습니다.");
    }

    @GetMapping("/comments/blocked")
    public ResponseEntity<BlockedCommentListResponse> getBlockedComments() {
        return ResponseEntity.ok(blockService.getBlockedComments());
    }

    // ========== 🔁 대댓글 차단 ==========

    @PostMapping("/replies/{replyId}/block")
    public ResponseEntity<MessageResponse> blockReply(@PathVariable Long replyId) {
        blockService.blockReply(replyId);
        return message("🛑 대댓글을 차단했습니다.");
    }

    @DeleteMapping("/replies/{replyId}/block")
    public ResponseEntity<MessageResponse> unblockReply(@PathVariable Long replyId) {
        blockService.unblockReply(replyId);
        return message("🔓 대댓글 차단을 해제했습니다.");
    }

    @GetMapping("/replies/blocked")
    public ResponseEntity<BlockedReplyListResponse> getBlockedReplies() {
        return ResponseEntity.ok(blockService.getBlockedReplies());
    }

    // ========== ✅ 공통 메시지 생성 ==========

    private ResponseEntity<MessageResponse> message(String message) {
        return ResponseEntity.ok(MessageResponse.of(message));
    }
}
