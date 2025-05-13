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
 * 신고(Report) 관련 JPA Repository
 * <p>
 * 중복 신고 방지, 관리자 필터링용으로 사용됩니다.
 * </p>
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /** 게시글 중복 신고 확인 */
    Optional<Report> findByReporterAndPost(User reporter, Post post);

    /** 댓글 중복 신고 확인 */
    Optional<Report> findByReporterAndComment(User reporter, Comment comment);

    /** 대댓글 중복 신고 확인 */
    Optional<Report> findByReporterAndReply(User reporter, Reply reply);

    /** 사용자 중복 신고 확인 */
    Optional<Report> findByReporterAndTargetUser(User reporter, User targetUser);

    /** 상태별 전체 신고 목록 조회 (예: PENDING, RESOLVED) */
    List<Report> findByStatus(String status);

    /** 게시글에 대한 신고 목록 */
    List<Report> findByPostIsNotNull();

    /** 댓글에 대한 신고 목록 */
    List<Report> findByCommentIsNotNull();

    /** 대댓글에 대한 신고 목록 */
    List<Report> findByReplyIsNotNull();

    /** 사용자에 대한 신고 목록 */
    List<Report> findByTargetUserIsNotNull();

    // 🔧 확장 고려: 최근 신고 순
    // List<Report> findByStatusOrderByReportedAtDesc(String status);
}
