package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.*;
import com.hamcam.back.service.community.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 리소스 신고 처리 컨트롤러 (단일 DTO 기반)
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** ✅ 게시글 신고 */
    @PostMapping("/posts/report")
    public ResponseEntity<MessageResponse> reportPost(@RequestBody PostReportRequest request) {
        reportService.reportPost(request);
        return ResponseEntity.ok(MessageResponse.of("게시글이 신고되었습니다."));
    }

    /** ✅ 댓글 신고 */
    @PostMapping("/comments/report")
    public ResponseEntity<MessageResponse> reportComment(@RequestBody CommentReportRequest request) {
        reportService.reportComment(request);
        return ResponseEntity.ok(MessageResponse.of("댓글이 신고되었습니다."));
    }

    /** ✅ 대댓글 신고 */
    @PostMapping("/replies/report")
    public ResponseEntity<MessageResponse> reportReply(@RequestBody ReplyReportRequest request) {
        reportService.reportReply(request);
        return ResponseEntity.ok(MessageResponse.of("대댓글이 신고되었습니다."));
    }

    /** ✅ 사용자 신고 */
    @PostMapping("/users/report")
    public ResponseEntity<MessageResponse> reportUser(@RequestBody UserReportRequest request) {
        reportService.reportUser(request);
        return ResponseEntity.ok(MessageResponse.of("사용자가 신고되었습니다."));
    }
}
