package com.hamcam.back.controller.community.block;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.block.request.BlockTargetRequest;
import com.hamcam.back.dto.community.block.request.UnblockTargetRequest;
import com.hamcam.back.dto.community.block.response.*;
import com.hamcam.back.service.community.block.BlockService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<MessageResponse> blockPost(@RequestBody BlockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.blockPost(request, httpRequest);
        return ok("🛑 게시글을 차단했습니다.");
    }

    @DeleteMapping("/posts")
    public ResponseEntity<MessageResponse> unblockPost(@RequestBody UnblockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.unblockPost(request, httpRequest);
        return ok("🔓 게시글 차단을 해제했습니다.");
    }

    @PostMapping("/posts/list")
    public ResponseEntity<BlockedPostListResponse> getBlockedPosts(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(blockService.getBlockedPosts(httpRequest));
    }

    // 💬 댓글 차단
    @PostMapping("/comments")
    public ResponseEntity<MessageResponse> blockComment(@RequestBody BlockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.blockComment(request, httpRequest);
        return ok("🛑 댓글을 차단했습니다.");
    }

    @DeleteMapping("/comments")
    public ResponseEntity<MessageResponse> unblockComment(@RequestBody UnblockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.unblockComment(request, httpRequest);
        return ok("🔓 댓글 차단을 해제했습니다.");
    }

    @PostMapping("/comments/list")
    public ResponseEntity<BlockedCommentListResponse> getBlockedComments(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(blockService.getBlockedComments(httpRequest));
    }

    // 🔁 대댓글 차단
    @PostMapping("/replies")
    public ResponseEntity<MessageResponse> blockReply(@RequestBody BlockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.blockReply(request, httpRequest);
        return ok("🛑 대댓글을 차단했습니다.");
    }

    @DeleteMapping("/replies")
    public ResponseEntity<MessageResponse> unblockReply(@RequestBody UnblockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.unblockReply(request, httpRequest);
        return ok("🔓 대댓글 차단을 해제했습니다.");
    }

    @PostMapping("/replies/list")
    public ResponseEntity<BlockedReplyListResponse> getBlockedReplies(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(blockService.getBlockedReplies(httpRequest));
    }

    // 👤 사용자 차단
    @PostMapping("/users")
    public ResponseEntity<MessageResponse> blockUser(@RequestBody BlockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.blockUser(request, httpRequest);
        return ok("🚫 사용자를 차단했습니다.");
    }

    @DeleteMapping("/users")
    public ResponseEntity<MessageResponse> unblockUser(@RequestBody UnblockTargetRequest request, HttpServletRequest httpRequest) {
        blockService.unblockUser(request, httpRequest);
        return ok("🔓 사용자 차단을 해제했습니다.");
    }

    @PostMapping("/users/list")
    public ResponseEntity<BlockedUserListResponse> getBlockedUsers(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(blockService.getBlockedUsers(httpRequest));
    }

    // ✅ 공통 메시지 응답
    private ResponseEntity<MessageResponse> ok(String msg) {
        return ResponseEntity.ok(MessageResponse.of(msg));
    }
}
