package com.hamcam.back.controller.friend;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.service.friend.FriendReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendReportController {

    private final FriendReportService friendReportService;

    /**
     * 사용자 신고 (친구 또는 일반 사용자)
     *
     * @param userId 신고 대상 사용자 ID
     * @param request 신고 요청 (신고자 ID, 사유 등)
     * @return 신고 완료 메시지
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
