package com.hamcam.back.service.friend;

import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendReport;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.friend.FriendReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 친구/사용자 신고 서비스
 * <p>
 * 친구 또는 일반 사용자를 대상으로 한 신고 요청을 처리합니다.
 * 중복 신고 및 자기자신 신고는 불가능하며, 신고 사유는 필수입니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FriendReportService {

    private final UserRepository userRepository;
    private final FriendReportRepository friendReportRepository;

    /**
     * 현재 인증된 사용자 ID 반환 (Mock)
     */
    private Long getCurrentUserId() {
        return 1L; // 추후 SecurityContextHolder 기반으로 변경
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    /**
     * 사용자 신고
     *
     * @param reportedUserId    신고 대상 사용자 ID
     * @param request           신고 요청 데이터 (사유 포함)
     */
    public void reportUser(Long reportedUserId, ReportRequest request) {
        User reporter = getCurrentUser();

        if (reporter.getId().equals(reportedUserId)) {
            throw new IllegalArgumentException("자기 자신은 신고할 수 없습니다.");
        }

        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new IllegalArgumentException("신고 대상 사용자가 존재하지 않습니다."));

        boolean alreadyReported = friendReportRepository
                .findByReporterAndReported(reporter, reported)
                .isPresent();

        if (alreadyReported) {
            throw new IllegalStateException("이미 신고한 사용자입니다.");
        }

        FriendReport report = FriendReport.builder()
                .reporter(reporter)
                .reported(reported)
                .reason(request.getReason())
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();

        friendReportRepository.save(report);
    }
}
