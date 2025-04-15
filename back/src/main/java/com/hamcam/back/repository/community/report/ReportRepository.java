package com.hamcam.back.repository.community.report;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 신고(Report) 관련 JPA Repository
 * <p>
 * 사용자가 게시글, 댓글, 대댓글, 다른 사용자를 신고한 내역을 저장하고,
 * 중복 신고 방지, 관리자 조회용 필터링 등에 사용됩니다.
 * </p>
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 게시글 + 사용자 중복 신고 여부 확인
     */
    Optional<Report> findByReporterAndPost(User reporter, Post post);

    /**
     * 댓글 + 사용자 중복 신고 여부 확인
     */
    Optional<Report> findByReporterAndComment(User reporter, Comment comment);

    /**
     * 대댓글 + 사용자 중복 신고 여부 확인
     */
    Optional<Report> findByReporterAndReply(User reporter, Reply reply);

    /**
     * 사용자 신고 중복 여부 확인
     */
    Optional<Report> findByReporterAndTargetUser(User reporter, User targetUser);

    // 관리자 전용

    /**
     * 처리 상태(status)에 따른 전체 신고 목록 조회
     */
    List<Report> findByStatus(String status);

    /**
     * 신고 유형별 목록 (post, comment, reply, user) 중 하나만 null 아님
     */
    List<Report> findByPostIsNotNull();

    List<Report> findByCommentIsNotNull();

    List<Report> findByReplyIsNotNull();

    List<Report> findByTargetUserIsNotNull();
}
