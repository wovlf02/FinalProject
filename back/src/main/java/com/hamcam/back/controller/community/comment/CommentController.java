package com.hamcam.back.controller.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.request.CommentUpdateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.service.community.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.hamcam.back.dto.common.MessageResponse;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> createComment(
            @PathVariable Long postId,
            @ModelAttribute CommentCreateRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.createComment(postId, request, files);
        return ResponseEntity.ok(new MessageResponse("댓글이 등록되었습니다."));
    }

    /**
     * 대댓글 등록
     */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<MessageResponse> createReply(
            @PathVariable Long commentId,
            @ModelAttribute CommentCreateRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.createReply(commentId, request, files);
        return ResponseEntity.ok(new MessageResponse("대댓글이 등록되었습니다."));
    }

    /**
     * 댓글 or 대댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> updateComment(
            @PathVariable Long commentId,
            @ModelAttribute CommentUpdateRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.updateComment(commentId, request, files);
        return ResponseEntity.ok(new MessageResponse("댓글이 수정되었습니다."));
    }

    /**
     * 댓글 or 대댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글이 삭제되었습니다."));
    }

    /**
     * 게시글 기준 전체 댓글 및 대댓글 조회 (계층 구조)
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponse> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    /**
     * 댓글 좋아요 추가
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글에 좋아요를 눌렀습니다."));
    }

    /**
     * 댓글 좋아요 취소
     */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> unlikeComment(@PathVariable Long commentId) {
        commentService.unlikeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 좋아요가 취소되었습니다."));
    }

    /**
     * 댓글 신고
     */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<MessageResponse> reportComment(
            @PathVariable Long commentId,
            @RequestBody String reason
    ) {
        commentService.reportComment(commentId, reason);
        return ResponseEntity.ok(new MessageResponse("해당 댓글이 신고되었습니다."));
    }

    /**
     * 댓글 차단
     */
    @PostMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> blockComment(@PathVariable Long commentId) {
        commentService.blockComment(commentId);
        return ResponseEntity.ok(new MessageResponse("해당 댓글이 차단되었습니다."));
    }

    /**
     * 댓글 차단 해제
     */
    @DeleteMapping("/comments/{commentId}/block")
    public ResponseEntity<MessageResponse> unblockComment(@PathVariable Long commentId) {
        commentService.unblockComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 차단이 해제되었습니다."));
    }

    /**
     * 차단된 댓글/대댓글 목록 조회
     */
    @GetMapping("/comments/blocked")
    public ResponseEntity<?> getBlockedComments() {
        return ResponseEntity.ok(commentService.getBlockedComments());
    }
}
