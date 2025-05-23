package com.hamcam.back.controller.community.comment;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.comment.request.*;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.service.community.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** ✅ 댓글 등록 */
    @PostMapping("/comments/create")
    public ResponseEntity<MessageResponse> createComment(@RequestBody CommentCreateRequest request) {
        Long commentId = commentService.createComment(request);
        return ResponseEntity.ok(MessageResponse.of("✅ 댓글이 등록되었습니다.", commentId));
    }

    /** ✅ 대댓글 등록 */
    @PostMapping("/replies/create")
    public ResponseEntity<MessageResponse> createReply(@RequestBody ReplyCreateRequest request) {
        Long replyId = commentService.createReply(request);
        return ResponseEntity.ok(MessageResponse.of("✅ 대댓글이 등록되었습니다.", replyId));
    }

    /** ✅ 댓글 수정 */
    @PutMapping("/comments/update")
    public ResponseEntity<MessageResponse> updateComment(@RequestBody CommentUpdateRequest request) {
        commentService.updateComment(request);
        return ResponseEntity.ok(MessageResponse.of("✏️ 댓글이 수정되었습니다."));
    }

    /** ✅ 대댓글 수정 */
    @PutMapping("/replies/update")
    public ResponseEntity<MessageResponse> updateReply(@RequestBody ReplyUpdateRequest request) {
        commentService.updateReply(request);
        return ResponseEntity.ok(MessageResponse.of("✏️ 대댓글이 수정되었습니다."));
    }

    /** ✅ 댓글 삭제 */
    @DeleteMapping("/comments/delete")
    public ResponseEntity<MessageResponse> deleteComment(@RequestBody CommentDeleteRequest request) {
        commentService.deleteComment(request);
        return ResponseEntity.ok(MessageResponse.of("🗑️ 댓글이 삭제되었습니다."));
    }

    /** ✅ 대댓글 삭제 */
    @DeleteMapping("/replies/delete")
    public ResponseEntity<MessageResponse> deleteReply(@RequestBody ReplyDeleteRequest request) {
        commentService.deleteReply(request);
        return ResponseEntity.ok(MessageResponse.of("🗑️ 대댓글이 삭제되었습니다."));
    }

    /** ✅ 게시글 기준 전체 댓글 + 대댓글 조회 */
    @PostMapping("/comments/by-post")
    public ResponseEntity<MessageResponse> getCommentsByPost(@RequestBody CommentListRequest request) {
        CommentListResponse response = commentService.getCommentsByPost(request);
        return ResponseEntity.ok(MessageResponse.of("💬 댓글 목록 조회 성공", response));
    }
}
