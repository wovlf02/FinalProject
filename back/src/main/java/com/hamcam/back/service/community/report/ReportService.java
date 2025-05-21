package com.hamcam.back.service.community.report;

import com.hamcam.back.dto.community.report.request.ReportRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    public void reportPost(Long postId, Long reporterId, ReportRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        User reporter = getUser(reporterId);

        if (reportRepository.findByReporterAndPost(reporter, post).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReason(), post, null, null, null);
    }

    public void reportComment(Long commentId, Long reporterId, ReportRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        User reporter = getUser(reporterId);

        if (reportRepository.findByReporterAndComment(reporter, comment).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReason(), null, comment, null, null);
    }

    public void reportReply(Long replyId, Long reporterId, ReportRequest request) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        User reporter = getUser(reporterId);

        if (reportRepository.findByReporterAndReply(reporter, reply).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReason(), null, null, reply, null);
    }

    public void reportUser(Long targetUserId, Long reporterId, ReportRequest request) {
        User reporter = getUser(reporterId);
        User target = getUser(targetUserId);

        if (reporter.getId().equals(targetUserId)) {
            throw new CustomException(ErrorCode.REPORT_SELF_NOT_ALLOWED);
        }

        if (reportRepository.findByReporterAndTargetUser(reporter, target).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReason(), null, null, null, target);
    }

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

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
