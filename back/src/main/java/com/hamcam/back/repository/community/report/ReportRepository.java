package com.hamcam.back.repository.community.report;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.entity.community.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [ReportRepository]
 *
 * 커뮤니티 신고(Report) 관련 JPA Repository입니다.
 * - 중복 신고 방지
 * - 관리자 전용 목록 필터링
 * - 신고 대상 유형별 조회(Post, Comment, Reply, User)
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    // ===== 중복 신고 방지용 단건 조회 =====

    /**
     * [게시글 신고 여부 확인]
     * 특정 사용자가 특정 게시글을 이미 신고했는지 확인합니다.
     *
     * @param reporter 신고자
     * @param post 신고 대상 게시글
     * @return 신고 정보 (Optional)
     */
    Optional<Report> findByReporterAndPost(User reporter, Post post);

    /**
     * [댓글 신고 여부 확인]
     * 특정 사용자가 특정 댓글을 이미 신고했는지 확인합니다.
     *
     * @param reporter 신고자
     * @param comment 신고 대상 댓글
     * @return 신고 정보 (Optional)
     */
    Optional<Report> findByReporterAndComment(User reporter, Comment comment);

    /**
     * [대댓글 신고 여부 확인]
     * 특정 사용자가 특정 대댓글을 이미 신고했는지 확인합니다.
     *
     * @param reporter 신고자
     * @param reply 신고 대상 대댓글
     * @return 신고 정보 (Optional)
     */
    Optional<Report> findByReporterAndReply(User reporter, Reply reply);

    /**
     * [사용자 신고 여부 확인]
     * 특정 사용자가 특정 유저를 신고한 적이 있는지 확인합니다.
     *
     * @param reporter 신고자
     * @param targetUser 신고 대상 사용자
     * @return 신고 정보 (Optional)
     */
    Optional<Report> findByReporterAndTargetUser(User reporter, User targetUser);

    // ===== 상태 기반 필터링 =====

    /**
     * [신고 상태별 전체 목록 조회]
     * 신고 상태(PENDING, RESOLVED 등)에 따라 필터링합니다.
     *
     * @param status 신고 상태
     * @return 상태에 해당하는 신고 목록
     */
    List<Report> findByStatus(String status);

    // ===== 신고 대상 유형별 필터링 =====

    /**
     * [게시글에 대한 신고 목록]
     *
     * @return 게시글 신고만 포함된 리스트
     */
    List<Report> findByPostIsNotNull();

    /**
     * [댓글에 대한 신고 목록]
     *
     * @return 댓글 신고만 포함된 리스트
     */
    List<Report> findByCommentIsNotNull();

    /**
     * [대댓글에 대한 신고 목록]
     *
     * @return 대댓글 신고만 포함된 리스트
     */
    List<Report> findByReplyIsNotNull();

    /**
     * [사용자에 대한 신고 목록]
     *
     * @return 사용자 신고만 포함된 리스트
     */
    List<Report> findByTargetUserIsNotNull();

    // === 확장 고려 ===
    // 최근 신고순 정렬 (예: 관리자용)
    // List<Report> findByStatusOrderByReportedAtDesc(String status);
}
