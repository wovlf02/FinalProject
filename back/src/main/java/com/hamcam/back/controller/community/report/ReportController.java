package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportWithUserRequest;
import com.hamcam.back.service.community.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 리소스 신고 처리 컨트롤러 (userId 포함 요청 DTO 방식)
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** 게시글 신고 */
    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<MessageResponse> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportWithUserRequest request
    ) {
        reportService.reportPost(request.getUserId(), postId, request.getReport());
        return ResponseEntity.ok(MessageResponse.of("게시글이 신고되었습니다."));
    }

    /** 댓글 신고 */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<MessageResponse> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportWithUserRequest request
    ) {
        reportService.reportComment(request.getUserId(), commentId, request.getReport());
        return ResponseEntity.ok(MessageResponse.of("댓글이 신고되었습니다."));
    }

    /** 대댓글 신고 */
    @PostMapping("/replies/{replyId}/report")
    public ResponseEntity<MessageResponse> reportReply(
            @PathVariable Long replyId,
            @RequestBody ReportWithUserRequest request
    ) {
        reportService.reportReply(request.getUserId(), replyId, request.getReport());
        return ResponseEntity.ok(MessageResponse.of("대댓글이 신고되었습니다."));
    }

    /** 사용자 신고 */
    @PostMapping("/users/{targetUserId}/report")
    public ResponseEntity<MessageResponse> reportUser(
            @PathVariable Long targetUserId,
            @RequestBody ReportWithUserRequest request
    ) {
        reportService.reportUser(request.getUserId(), targetUserId, request.getReport());
        return ResponseEntity.ok(MessageResponse.of("사용자가 신고되었습니다."));
    }
}
