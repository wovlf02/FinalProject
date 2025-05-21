package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.community.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 리소스 신고 처리 컨트롤러 (보안 제거 + userId 전달 방식 확장)
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** 게시글 신고 */
    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<MessageResponse> reportPost(
            @RequestParam("userId") Long reporterId,
            @PathVariable Long postId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportPost(reporterId, postId, request);
        return ResponseEntity.ok(MessageResponse.of("게시글이 신고되었습니다."));
    }

    /** 댓글 신고 */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<MessageResponse> reportComment(
            @RequestParam("userId") Long reporterId,
            @PathVariable Long commentId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportComment(reporterId, commentId, request);
        return ResponseEntity.ok(MessageResponse.of("댓글이 신고되었습니다."));
    }

    /** 대댓글 신고 */
    @PostMapping("/replies/{replyId}/report")
    public ResponseEntity<MessageResponse> reportReply(
            @RequestParam("userId") Long reporterId,
            @PathVariable Long replyId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportReply(reporterId, replyId, request);
        return ResponseEntity.ok(MessageResponse.of("대댓글이 신고되었습니다."));
    }

    /** 사용자 신고 */
    @PostMapping("/users/{targetUserId}/report")
    public ResponseEntity<MessageResponse> reportUser(
            @RequestParam("userId") Long reporterId,
            @PathVariable Long targetUserId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportUser(reporterId, targetUserId, request);
        return ResponseEntity.ok(MessageResponse.of("사용자가 신고되었습니다."));
    }

    // ====================== 관리자 기능 예시 ======================
    // @GetMapping("/reports") → 전체 신고 목록 조회
    // @PatchMapping("/reports/{reportId}/resolve") → 신고 상태 처리 (승인/반려 등)
}
