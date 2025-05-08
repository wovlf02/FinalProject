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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 등록 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<MessageResponse> createComment(
            @PathVariable Long postId,
            @ModelAttribute CommentCreateRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.createComment(postId, request, files);
        return ResponseEntity.ok(new MessageResponse("댓글이 등록되었습니다."));
    }

    /** 대댓글 등록 */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<MessageResponse> createReply(
            @PathVariable Long commentId,
            @ModelAttribute ReplyCreateRequest request, // ✅ 여기를 수정
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.createReply(commentId, request, files);
        return ResponseEntity.ok(new MessageResponse("대댓글이 등록되었습니다."));
    }


    /** 댓글 or 대댓글 수정 */
    @PutMapping("/comments/{commentId}/update")
    public ResponseEntity<MessageResponse> updateComment(
            @PathVariable Long commentId,
            @ModelAttribute CommentUpdateRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        commentService.updateComment(commentId, request, files);
        return ResponseEntity.ok(new MessageResponse("댓글이 수정되었습니다."));
    }

    /** 댓글 or 대댓글 삭제 */
    @DeleteMapping("/comments/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글이 삭제되었습니다."));
    }

    /** 게시글 기준 전체 댓글 및 대댓글 조회 (계층 구조) */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentListResponse> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    /** 댓글 좋아요 추가 */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글에 좋아요를 눌렀습니다."));
    }

    /** 댓글 좋아요 취소 */
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<MessageResponse> unlikeComment(@PathVariable Long commentId) {
        commentService.unlikeComment(commentId);
        return ResponseEntity.ok(new MessageResponse("댓글 좋아요가 취소되었습니다."));
    }


}
