package com.hamcam.back.controller.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.request.CommentUpdateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.dto.community.reply.request.ReplyCreateRequest;
import com.hamcam.back.service.community.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 등록 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        commentService.createComment(postId, request);
        return ResponseEntity.ok(new MessageResponse("댓글이 등록되었습니다."));
    }

    /** 대댓글 등록 */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<MessageResponse> createReply(
            @PathVariable Long commentId,
            @RequestBody ReplyCreateRequest request
    ) {
        commentService.createReply(commentId, request);
        return ResponseEntity.ok(new MessageResponse("대댓글이 등록되었습니다."));
    }

    /** 댓글 수정 */
    @PutMapping("/comments/{commentId}/update")
    public ResponseEntity<MessageResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(new MessageResponse("댓글이 수정되었습니다."));
    }

    /** 대댓글 수정 */
    @PutMapping("/replies/{replyId}/update")
    public ResponseEntity<MessageResponse> updateReply(
            @PathVariable Long replyId,
            @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateReply(replyId, request.getContent());
        return ResponseEntity.ok(new MessageResponse("대댓글이 수정되었습니다."));
    }

    /** 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글이 삭제되었습니다."));
    }

    /** 대댓글 삭제 */
    @DeleteMapping("/replies/{replyId}/delete")
    public ResponseEntity<MessageResponse> deleteReply(@PathVariable Long replyId) {
        commentService.deleteReply(replyId);
        return ResponseEntity.ok(new MessageResponse("대댓글이 삭제되었습니다."));
    }

    /** 게시글 기준 전체 댓글 및 대댓글 조회 (계층 구조) */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponse> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }
}
