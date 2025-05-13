package com.hamcam.back.service.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.dto.community.reply.request.ReplyCreateRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.attachment.AttachmentRepository;
import com.hamcam.back.repository.community.block.BlockRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.like.LikeRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hamcam.back.global.security.SecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BlockRepository blockRepository;
    private final ReportRepository reportRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    private User getCurrentUserEntity() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /** 댓글 등록 */
    public void createComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .writer(getCurrentUserEntity())
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
    }

    /** 대댓글 등록 */
    public void createReply(Long commentId, ReplyCreateRequest request) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = Reply.builder()
                .comment(parent)
                .writer(getCurrentUserEntity())
                .post(parent.getPost())
                .content(request.getContent())
                .build();

        replyRepository.save(reply);
    }

    /** 댓글 수정 */
    public void updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.updateContent(newContent);
    }

    /** 대댓글 수정 */
    public void updateReply(Long replyId, String newContent) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        reply.updateContent(newContent);
    }

    /** 댓글 삭제 */
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.softDelete();
    }

    /** 대댓글 삭제 */
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        reply.softDelete();
    }

    /** 게시글 기준 전체 댓글 및 대댓글 계층 조회 */
    public CommentListResponse getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Long userId = getCurrentUserId();

        List<Comment> comments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post);
        Map<Long, List<Reply>> replyMap = replyRepository.findByPostAndIsDeletedFalse(post).stream()
                .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentResponse> result = comments.stream()
                .map(c -> CommentResponse.from(c, replyMap.getOrDefault(c.getId(), List.of()), userId))
                .collect(Collectors.toList());

        return new CommentListResponse(result);
    }

    /** 댓글 좋아요 */
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = getCurrentUserEntity();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(l -> { throw new CustomException(ErrorCode.DUPLICATE_LIKE); });

        comment.increaseLikeCount();
        likeRepository.save(Like.builder().user(user).comment(comment).build());
    }

    /** 댓글 좋아요 취소 */
    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = getCurrentUserEntity();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(like -> {
                    comment.decreaseLikeCount();
                    likeRepository.delete(like);
                });
    }

    /** 대댓글 좋아요 */
    public void likeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        User user = getCurrentUserEntity();

        likeRepository.findByUserAndReply(user, reply)
                .ifPresent(l -> { throw new CustomException(ErrorCode.DUPLICATE_LIKE); });

        reply.increaseLikeCount();
        likeRepository.save(Like.builder().user(user).reply(reply).build());
    }

    /** 대댓글 좋아요 취소 */
    public void unlikeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        User user = getCurrentUserEntity();

        likeRepository.findByUserAndReply(user, reply)
                .ifPresent(like -> {
                    reply.decreaseLikeCount();
                    likeRepository.delete(like);
                });
    }

    /** 댓글 신고 */
    public void reportComment(Long commentId, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = getCurrentUserEntity();

        reportRepository.findByReporterAndComment(user, comment)
                .ifPresent(r -> { throw new CustomException(ErrorCode.DUPLICATE_REPORT); });

        reportRepository.save(Report.builder()
                .reporter(user)
                .comment(comment)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build());
    }

    /** 댓글 차단 또는 차단 해제 */
    public void blockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = getCurrentUserEntity();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        block -> {
                            block.restore(); // 차단 해제
                            blockRepository.save(block);
                        },
                        () -> blockRepository.save(Block.builder().user(user).comment(comment).build())
                );
    }

    /** 댓글 차단 해제 (완전한 unblock) */
    public void unblockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = getCurrentUserEntity();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(block -> {
                    block.softDelete();
                    blockRepository.save(block);
                });
    }

    /** 차단한 댓글 목록 조회 */
    public List<CommentResponse> getBlockedComments() {
        User user = getCurrentUserEntity();

        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNullAndIsDeletedFalse(user);

        return blocks.stream()
                .map(block -> {
                    Comment comment = block.getComment();
                    List<Reply> replies = replyRepository.findByCommentId(comment.getId());
                    return CommentResponse.from(comment, replies, user.getId());
                })
                .collect(Collectors.toList());
    }
}
