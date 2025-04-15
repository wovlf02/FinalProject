package com.hamcam.back.service.community.report;

import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 신고 처리 서비스
 * <p>
 * 게시글, 댓글, 대댓글, 사용자를 대상으로 신고를 생성하고 중복을 방지합니다.
 * </p>
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
     * 현재 로그인된 사용자 ID (mock)
     */
    private Long getCurrentUserId() {
        return 1L;
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    // 게시글 신고
    public void reportPost(Long postId, ReportRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        reportRepository.findByReporterAndPost(reporter, post)
                .ifPresent(r -> { throw new IllegalStateException("이미 신고한 게시글입니다."); });

        Report report = Report.builder()
                .reporter(reporter)
                .post(post)
                .reason(request.getReason())
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        reportRepository.save(report);
    }

    // 댓글 신고
    public void reportComment(Long commentId, ReportRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        reportRepository.findByReporterAndComment(reporter, comment)
                .ifPresent(r -> { throw new IllegalStateException("이미 신고한 댓글입니다."); });

        Report report = Report.builder()
                .reporter(reporter)
                .comment(comment)
                .reason(request.getReason())
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        reportRepository.save(report);
    }

    // 대댓글 신고
    public void reportReply(Long replyId, ReportRequest request) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));
        User reporter = getCurrentUser();

        reportRepository.findByReporterAndReply(reporter, reply)
                .ifPresent(r -> { throw new IllegalStateException("이미 신고한 대댓글입니다."); });

        Report report = Report.builder()
                .reporter(reporter)
                .reply(reply)
                .reason(request.getReason())
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        reportRepository.save(report);
    }

    // 사용자 신고
    public void reportUser(Long userId, ReportRequest request) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        User reporter = getCurrentUser();

        if (reporter.getId().equals(userId)) {
            throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
        }

        reportRepository.findByReporterAndTargetUser(reporter, target)
                .ifPresent(r -> { throw new IllegalStateException("이미 신고한 사용자입니다."); });

        Report report = Report.builder()
                .reporter(reporter)
                .targetUser(target)
                .reason(request.getReason())
                .reportedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        reportRepository.save(report);
    }
}
