package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.community.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 신고
     */
    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<MessageResponse> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportPost(postId, request);
        return ResponseEntity.ok(new MessageResponse("해당 게시글이 신고되었습니다."));
    }

    /**
     * 댓글 신고
     */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<MessageResponse> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportComment(commentId, request);
        return ResponseEntity.ok(new MessageResponse("해당 댓글이 신고되었습니다."));
    }

    /**
     * 대댓글 신고
     */
    @PostMapping("/replies/{replyId}/report")
    public ResponseEntity<MessageResponse> reportReply(
            @PathVariable Long replyId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportReply(replyId, request);
        return ResponseEntity.ok(new MessageResponse("해당 대댓글이 신고되었습니다."));
    }

    /**
     * 사용자 신고
     */
    @PostMapping("/users/{userId}/report")
    public ResponseEntity<MessageResponse> reportUser(
            @PathVariable Long userId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportUser(userId, request);
        return ResponseEntity.ok(new MessageResponse("해당 사용자가 신고되었습니다."));
    }
}
