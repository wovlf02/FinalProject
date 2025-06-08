package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [FriendReportRepository]
 *
 * 사용자 신고(FriendReport) 관련 JPA Repository입니다.
 * - 사용자가 다른 사용자를 신고한 이력을 저장합니다.
 * - 중복 신고 방지, 전체 신고 목록 확인 등 관리자 처리 용도로 사용됩니다.
 */
public interface FriendReportRepository extends JpaRepository<FriendReport, Long> {

    /**
     * [중복 신고 여부 확인]
     * 특정 사용자가 동일한 상대를 이미 신고했는지 여부를 확인합니다.
     *
     * @param reporter 신고한 사용자
     * @param reported 신고당한 사용자
     * @return 신고 정보 (Optional)
     */
    Optional<FriendReport> findByReporterAndReported(User reporter, User reported);

    /**
     * [전체 신고 목록 조회]
     * 관리자가 모든 사용자 간 신고 내역을 확인할 때 사용됩니다.
     *
     * @return 사용자 신고 리스트
     */
    List<FriendReport> findAll();
}
