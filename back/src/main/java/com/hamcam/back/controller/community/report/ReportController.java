package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.*;
import com.hamcam.back.service.community.report.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 리소스 신고 처리 컨트롤러 (세션 기반)
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** ✅ 게시글 신고 */
    @PostMapping("/posts/report")
    public ResponseEntity<MessageResponse> reportPost(
            @RequestBody PostReportRequest request,
            HttpServletRequest httpRequest
    ) {
        reportService.reportPost(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("게시글이 신고되었습니다."));
    }

    /** ✅ 댓글 신고 */
    @PostMapping("/comments/report")
    public ResponseEntity<MessageResponse> reportComment(
            @RequestBody CommentReportRequest request,
            HttpServletRequest httpRequest
    ) {
        reportService.reportComment(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("댓글이 신고되었습니다."));
    }

    /** ✅ 대댓글 신고 */
    @PostMapping("/replies/report")
    public ResponseEntity<MessageResponse> reportReply(
            @RequestBody ReplyReportRequest request,
            HttpServletRequest httpRequest
    ) {
        reportService.reportReply(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("대댓글이 신고되었습니다."));
    }

    /** ✅ 사용자 신고 */
    @PostMapping("/users/report")
    public ResponseEntity<MessageResponse> reportUser(
            @RequestBody UserReportRequest request,
            HttpServletRequest httpRequest
    ) {
        reportService.reportUser(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("사용자가 신고되었습니다."));
    }
}
