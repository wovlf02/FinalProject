package com.hamcam.back.controller.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.request.CommentUpdateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.reply.request.ReplyCreateRequest;
import com.hamcam.back.service.community.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 등록 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> createComment(
            @PathVariable Long postId,
            @RequestParam("userId") Long userId,
            @RequestBody CommentCreateRequest request
    ) {
        Long commentId = commentService.createComment(postId, userId, request);
        return ResponseEntity.ok(MessageResponse.of("댓글이 등록되었습니다.", commentId));
    }

    /** 대댓글 등록 */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<MessageResponse> createReply(
            @PathVariable Long commentId,
            @RequestParam("userId") Long userId,
            @RequestBody ReplyCreateRequest request
    ) {
        Long replyId = commentService.createReply(commentId, userId, request);
        return ResponseEntity.ok(MessageResponse.of("대댓글이 등록되었습니다.", replyId));
    }

    /** 댓글 수정 */
    @PutMapping("/comments/{commentId}/update")
    public ResponseEntity<MessageResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam("userId") Long userId,
            @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(commentId, userId, request.getContent());
        return ResponseEntity.ok(MessageResponse.of("댓글이 수정되었습니다."));
    }

    /** 대댓글 수정 */
    @PutMapping("/replies/{replyId}/update")
    public ResponseEntity<MessageResponse> updateReply(
            @PathVariable Long replyId,
            @RequestParam("userId") Long userId,
            @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateReply(replyId, userId, request.getContent());
        return ResponseEntity.ok(MessageResponse.of("대댓글이 수정되었습니다."));
    }

    /** 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(
            @PathVariable Long commentId,
            @RequestParam("userId") Long userId
    ) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(MessageResponse.of("댓글이 삭제되었습니다."));
    }

    /** 대댓글 삭제 */
    @DeleteMapping("/replies/{replyId}/delete")
    public ResponseEntity<MessageResponse> deleteReply(
            @PathVariable Long replyId,
            @RequestParam("userId") Long userId
    ) {
        commentService.deleteReply(replyId, userId);
        return ResponseEntity.ok(MessageResponse.of("대댓글이 삭제되었습니다."));
    }

    /** 게시글 기준 전체 댓글 및 대댓글 조회 */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> getCommentsByPost(@PathVariable Long postId) {
        CommentListResponse response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(MessageResponse.of("댓글 목록 조회 성공", response));
    }
}
