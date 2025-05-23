package com.hamcam.back.controller.community.block;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.block.request.BlockTargetRequest;
import com.hamcam.back.dto.community.block.request.UnblockTargetRequest;
import com.hamcam.back.dto.community.block.request.UserOnlyRequest;
import com.hamcam.back.dto.community.block.response.*;
import com.hamcam.back.service.community.block.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [BlockController]
 * 커뮤니티 내 게시글/댓글/대댓글/사용자 차단 및 해제, 차단 목록 조회 기능 제공
 */
@RestController
@RequestMapping("/api/community/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    // 📌 게시글 차단
    @PostMapping("/posts")
    public ResponseEntity<MessageResponse> blockPost(@RequestBody BlockTargetRequest request) {
        blockService.blockPost(request);
        return ok("🛑 게시글을 차단했습니다.");
    }

    @DeleteMapping("/posts")
    public ResponseEntity<MessageResponse> unblockPost(@RequestBody UnblockTargetRequest request) {
        blockService.unblockPost(request);
        return ok("🔓 게시글 차단을 해제했습니다.");
    }

    @PostMapping("/posts/list")
    public ResponseEntity<BlockedPostListResponse> getBlockedPosts(@RequestBody UserOnlyRequest request) {
        return ResponseEntity.ok(blockService.getBlockedPosts(request));
    }

    // 💬 댓글 차단
    @PostMapping("/comments")
    public ResponseEntity<MessageResponse> blockComment(@RequestBody BlockTargetRequest request) {
        blockService.blockComment(request);
        return ok("🛑 댓글을 차단했습니다.");
    }

    @DeleteMapping("/comments")
    public ResponseEntity<MessageResponse> unblockComment(@RequestBody UnblockTargetRequest request) {
        blockService.unblockComment(request);
        return ok("🔓 댓글 차단을 해제했습니다.");
    }

    @PostMapping("/comments/list")
    public ResponseEntity<BlockedCommentListResponse> getBlockedComments(@RequestBody UserOnlyRequest request) {
        return ResponseEntity.ok(blockService.getBlockedComments(request));
    }

    // 🔁 대댓글 차단
    @PostMapping("/replies")
    public ResponseEntity<MessageResponse> blockReply(@RequestBody BlockTargetRequest request) {
        blockService.blockReply(request);
        return ok("🛑 대댓글을 차단했습니다.");
    }

    @DeleteMapping("/replies")
    public ResponseEntity<MessageResponse> unblockReply(@RequestBody UnblockTargetRequest request) {
        blockService.unblockReply(request);
        return ok("🔓 대댓글 차단을 해제했습니다.");
    }

    @PostMapping("/replies/list")
    public ResponseEntity<BlockedReplyListResponse> getBlockedReplies(@RequestBody UserOnlyRequest request) {
        return ResponseEntity.ok(blockService.getBlockedReplies(request));
    }

    // 👤 사용자 차단
    @PostMapping("/users")
    public ResponseEntity<MessageResponse> blockUser(@RequestBody BlockTargetRequest request) {
        blockService.blockUser(request);
        return ok("🚫 사용자를 차단했습니다.");
    }

    @DeleteMapping("/users")
    public ResponseEntity<MessageResponse> unblockUser(@RequestBody UnblockTargetRequest request) {
        blockService.unblockUser(request);
        return ok("🔓 사용자 차단을 해제했습니다.");
    }

    @PostMapping("/users/list")
    public ResponseEntity<BlockedUserListResponse> getBlockedUsers(@RequestBody UserOnlyRequest request) {
        return ResponseEntity.ok(blockService.getBlockedUsers(request));
    }

    // ✅ 공통 메시지
    private ResponseEntity<MessageResponse> ok(String msg) {
        return ResponseEntity.ok(MessageResponse.of(msg));
    }
}
