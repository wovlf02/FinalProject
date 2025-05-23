package com.hamcam.back.service.community.report;

import com.hamcam.back.dto.community.report.request.*;
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

    /** ✅ 게시글 신고 */
    public void reportPost(PostReportRequest request) {
        User reporter = getUser(request.getUserId());
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (reportRepository.findByReporterAndPost(reporter, post).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReport(), post, null, null, null);
    }

    /** ✅ 댓글 신고 */
    public void reportComment(CommentReportRequest request) {
        User reporter = getUser(request.getUserId());
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (reportRepository.findByReporterAndComment(reporter, comment).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReport(), null, comment, null, null);
    }

    /** ✅ 대댓글 신고 */
    public void reportReply(ReplyReportRequest request) {
        User reporter = getUser(request.getUserId());
        Reply reply = replyRepository.findById(request.getReplyId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        if (reportRepository.findByReporterAndReply(reporter, reply).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReport(), null, null, reply, null);
    }

    /** ✅ 사용자 신고 */
    public void reportUser(UserReportRequest request) {
        User reporter = getUser(request.getUserId());
        User target = getUser(request.getTargetUserId());

        if (reporter.getId().equals(target.getId())) {
            throw new CustomException(ErrorCode.REPORT_SELF_NOT_ALLOWED);
        }

        if (reportRepository.findByReporterAndTargetUser(reporter, target).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        createReport(reporter, request.getReport(), null, null, null, target);
    }

    /** ✅ 신고 생성 공통 처리 */
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

    /** ✅ 유저 조회 공통 메서드 */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
