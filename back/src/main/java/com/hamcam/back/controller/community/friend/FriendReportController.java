package com.hamcam.back.controller.community.friend;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.community.friend.FriendReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 친구 및 사용자 신고 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Validated
public class FriendReportController {

    private final FriendReportService friendReportService;

    /**
     * 특정 사용자 신고 요청 처리
     *
     * @param userId 신고 대상 사용자 ID
     * @param request 신고 요청 내용 (신고 사유 등)
     * @return 신고 처리 결과 메시지
     */
    @PostMapping("/report/{userId}")
    public ResponseEntity<MessageResponse> reportUser(
            @PathVariable Long userId,
            @RequestBody ReportRequest request
    ) {
        friendReportService.reportUser(userId, request);
        return ResponseEntity.ok(new MessageResponse("해당 사용자가 신고되었습니다."));
    }
}
