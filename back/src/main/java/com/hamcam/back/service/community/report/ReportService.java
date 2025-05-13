package com.hamcam.back.service.community.report;

import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 커뮤니티 리소스 신고 서비스
 * - 게시글, 댓글, 대댓글, 사용자 신고 처리 및 중복 방지
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 엔티티 반환
     */
    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자 인증 정보가 유효하지 않습니다."));
    }

    /**
     * 게시글 신고
     */
    public void reportPost(Long postId, ReportRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("해당 게시글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        if (reportRepository.findByReporterAndPost(reporter, post).isPresent()) {
            throw new CustomException("이미 이 게시글을 신고했습니다.");
        }

        createReport(reporter, request.getReason(), post, null, null, null);
    }

    /**
     * 댓글 신고
     */
    public void reportComment(Long commentId, ReportRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("해당 댓글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        if (reportRepository.findByReporterAndComment(reporter, comment).isPresent()) {
            throw new CustomException("이미 이 댓글을 신고했습니다.");
        }

        createReport(reporter, request.getReason(), null, comment, null, null);
    }

    /**
     * 대댓글 신고
     */
    public void reportReply(Long replyId, ReportRequest request) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException("해당 대댓글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        if (reportRepository.findByReporterAndReply(reporter, reply).isPresent()) {
            throw new CustomException("이미 이 대댓글을 신고했습니다.");
        }

        createReport(reporter, request.getReason(), null, null, reply, null);
    }

    /**
     * 사용자 신고
     */
    public void reportUser(Long userId, ReportRequest request) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("신고 대상 사용자가 존재하지 않습니다."));
        User reporter = getCurrentUser();

        if (reporter.getId().equals(userId)) {
            throw new CustomException("자기 자신은 신고할 수 없습니다.");
        }

        if (reportRepository.findByReporterAndTargetUser(reporter, target).isPresent()) {
            throw new CustomException("이미 이 사용자를 신고했습니다.");
        }

        createReport(reporter, request.getReason(), null, null, null, target);
    }

    /**
     * 신고 생성 공통 메서드
     */
    private void createReport(User reporter, String reason,
                              Post post, Comment comment, Reply reply, User targetUser) {

        Report report = Report.builder()
                .reporter(reporter)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .post(post)
                .comment(comment)
                .reply(reply)
                .targetUser(targetUser)
                .build();

        reportRepository.save(report);
    }
}
