package com.studymate.back.controller;

import com.studymate.back.dto.CommentRequest;
import com.studymate.back.dto.CommentResponse;
import com.studymate.back.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 컨트롤러
 *
 * 댓글 작성, 수정, 삭제
 * 댓글 조회 및 신고
 * 대댓글 작성, 수정, 삭제
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ====================== 댓글 관리 API ========================

    /**
     * 댓글 작성 API
     * 사용자가 특정 게시글에 댓글을 작성할 수 있음
     * @param postId    댓글을 작성할 게시글 ID
     * @param request   댓글 작성 요청 데이터
     * @return  작성된 댓글 정보
     */
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(postId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정 API
     * 사용자가 자신이 작성한 댓글을 수정할 수 있음
     * @param commentId 수정할 댓글 ID
     * @param request   수정할 댓글 데이터
     * @return  수정된 댓글 정보
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제 API
     * 사용자가 자신이 작성한 댓글을 삭제할 수 있음
     * 삭제된 댓글은 '삭제된 댓글입니다.'로 표시됨
     * @param commentId 삭제할 댓글 ID
     * @return  성공 시 200 OK 반환
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글의 댓글 목록 조회 API
     * 사용자가 특정 게시글의 댓글을 조회할 수 있음
     * @param postId    조회할 게시글 ID
     * @return  해당 게시글의 댓글 목록
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponse> response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 신고 API
     * 사용자가 특정 댓글을 신고할 수 있음
     * @param commentId 신고할 댓글 ID
     * @param request   신고 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/{commentId}/report")
    public ResponseEntity<Void> reportComment(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        commentService.reportComment(commentId, request);
        return ResponseEntity.ok().build();
    }

    // ================== 대댓글 관리 API ===================

    /**
     * 대댓글 작성 API
     * 사용자가 특정 댓글에 대한 대댓글을 작성할 수 있음
     * @param commentId 대댓글을 작성할 댓글 ID
     * @param request   대댓글 작성 요청 데이터
     * @return  작성된 대댓글 정보
     */
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<CommentResponse> createReply(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createReply(commentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 대댓글 수정 API
     * 사용자가 자신이 작성한 대댓글을 수정할 수 있음
     * @param replyId   수정할 대댓글 ID
     * @param request   수정할 대댓글 데이터
     * @return  수정된 대댓글 정보
     */
    @PutMapping("/reply/{replyId}")
    public ResponseEntity<CommentResponse> updateReply(@PathVariable Long replyId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateReply(replyId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 대댓글 삭제 API
     * 사용자가 자신이 작성한 대댓글을 삭제할 수 있음
     * 삭제된 대댓글은 '삭제된 대댓글입니다.'로 표시됨
     * @param replyId
     * @return
     */
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        commentService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }

    /**
     * 대댓글 목록 조회 API
     * 특정 댓글에 달린 대댓글을 조회할 수 있음
     * @param commentId 조회할 댓글 ID
     * @return  해당 댓글의 대댓글 목록
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getRepliesByComment(@PathVariable Long commentId) {
        List<CommentResponse> response = commentService.getRepliesByComment(commentId);
        return ResponseEntity.ok(response);
    }

    /**
     * 대댓글 신고 API
     * 사용자가 특정 대댓글을 신고할 수 있음
     * @param replyId   신고할 대댓글 ID
     * @param request   신고 요청 데이터
     * @return  성공 시 200 OK 반환
     */
    @PostMapping("/reply/{replyId}/report")
    public ResponseEntity<Void> reportReply(@PathVariable Long replyId, @RequestBody CommentRequest request) {
        commentService.reportReply(replyId, request);
        return ResponseEntity.ok().build();
    }
}
