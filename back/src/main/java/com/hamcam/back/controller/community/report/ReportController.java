package com.hamcam.back.controller.community.report;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.community.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 리소스 신고 처리 컨트롤러
 * - 게시글, 댓글, 대댓글, 사용자 신고 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 신고
     *
     * @param postId 신고 대상 게시글 ID
     * @param request 신고 사유 정보
     * @return 신고 처리 결과
     */
    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<MessageResponse> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportPost(postId, request);
        return ResponseEntity.ok(new MessageResponse("게시글이 신고되었습니다."));
    }

    /**
     * 댓글 신고
     *
     * @param commentId 신고 대상 댓글 ID
     * @param request 신고 사유 정보
     * @return 신고 처리 결과
     */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<MessageResponse> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportComment(commentId, request);
        return ResponseEntity.ok(new MessageResponse("댓글이 신고되었습니다."));
    }

    /**
     * 대댓글 신고
     *
     * @param replyId 신고 대상 대댓글 ID
     * @param request 신고 사유 정보
     * @return 신고 처리 결과
     */
    @PostMapping("/replies/{replyId}/report")
    public ResponseEntity<MessageResponse> reportReply(
            @PathVariable Long replyId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportReply(replyId, request);
        return ResponseEntity.ok(new MessageResponse("대댓글이 신고되었습니다."));
    }

    /**
     * 사용자 신고
     *
     * @param userId 신고 대상 사용자 ID
     * @param request 신고 사유 정보
     * @return 신고 처리 결과
     */
    @PostMapping("/users/{userId}/report")
    public ResponseEntity<MessageResponse> reportUser(
            @PathVariable Long userId,
            @RequestBody ReportRequest request
    ) {
        reportService.reportUser(userId, request);
        return ResponseEntity.ok(new MessageResponse("사용자가 신고되었습니다."));
    }

    // ====================== 관리자 기능 예시 ======================

    // @GetMapping("/reports") → 전체 신고 목록 조회
    // @PatchMapping("/reports/{reportId}/resolve") → 신고 상태 처리 (승인/반려 등)
}
