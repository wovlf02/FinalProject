package com.hamcam.back.service.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.dto.community.reply.request.ReplyCreateRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final SecurityUtil securityUtil;

    /** 댓글 등록 */
    public Long createComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User user = securityUtil.getCurrentUser();

        Comment comment = Comment.builder()
                .post(post)
                .writer(user)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);

        post.incrementCommentCount();
        return saved.getId();
    }

    /** 대댓글 등록 */
    public Long createReply(Long commentId, ReplyCreateRequest request) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = securityUtil.getCurrentUser();

        Reply reply = Reply.builder()
                .comment(parent)
                .post(parent.getPost())
                .writer(user)
                .content(request.getContent())
                .build();

        Reply saved = replyRepository.save(reply);

        parent.getPost().incrementCommentCount();
        return saved.getId();
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

        comment.getPost().decrementCommentCount();
    }

    /** 대댓글 삭제 */
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        reply.softDelete();

        reply.getPost().decrementCommentCount();
    }

    /** 게시글 기준 댓글 + 대댓글 계층 조회 */
    public CommentListResponse getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Long currentUserId = securityUtil.getCurrentUserId();

        List<Comment> comments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post);

        Map<Long, List<Reply>> replyMap = replyRepository.findByPostAndIsDeletedFalse(post).stream()
                .collect(Collectors.groupingBy(reply -> reply.getComment().getId()));

        List<CommentResponse> responseList = comments.stream()
                .map(comment -> CommentResponse.from(comment, replyMap.getOrDefault(comment.getId(), List.of()), currentUserId))
                .collect(Collectors.toList());

        return new CommentListResponse(responseList);
    }
}
