package com.hamcam.back.service.community.friend;

import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.ReportStatus;
import com.hamcam.back.entity.friend.FriendReport;
import com.hamcam.back.entity.friend.FriendReportStatus;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.friend.FriendReportRepository;
import com.hamcam.back.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 친구 및 일반 사용자 신고 서비스
 */
@Service
@RequiredArgsConstructor
public class FriendReportService {

    private final UserRepository userRepository;
    private final FriendReportRepository friendReportRepository;

    /**
     * 현재 로그인한 사용자 ID (mock 구현)
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new CustomException("사용자 정보를 불러올 수 없습니다.");
    }

    private User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자 정보를 불러올 수 없습니다."));
    }

    /**
     * 사용자 신고 처리
     *
     * @param reportedUserId 신고 대상 사용자 ID
     * @param request 신고 요청 (신고 사유 포함)
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
            throw new IllegalStateException("이미 해당 사용자를 신고했습니다.");
        }

        FriendReport report = FriendReport.builder()
                .reporter(reporter)
                .reported(reported)
                .reason(request.getReason())
                .status(FriendReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build();

        friendReportRepository.save(report);
    }
}
