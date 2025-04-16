package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 신고(FriendReport) JPA Repository
 * <p>
 * 사용자가 다른 사용자를 신고한 내역을 저장합니다.
 * 중복 신고 방지, 신고 목록 조회, 관리자 신고 처리에 사용됩니다.
 * </p>
 */
public interface FriendReportRepository extends JpaRepository<FriendReport, Long> {

    /**
     * 특정 사용자 간 중복 신고 여부 확인
     */
    Optional<FriendReport> findByReporterAndReported(User reporter, User reported);

    /**
     * 내가 신고한 사용자 목록 조회
     */
    List<FriendReport> findByReporter(User reporter);

    /**
     * 나를 신고한 사용자 목록 조회
     */
    List<FriendReport> findByReported(User reported);

    /**
     * 관리자 - 전체 신고 목록 조회
     */
    List<FriendReport> findAll();
}
