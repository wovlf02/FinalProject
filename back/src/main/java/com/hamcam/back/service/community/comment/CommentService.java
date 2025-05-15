package com.hamcam.back.service.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.dto.community.reply.request.ReplyCreateRequest;
import com.hamcam.back.entity.community.*;
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
    private final SecurityUtil securityUtil; // ✅ 추가

    /** 댓글 등록 */
    public void createComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .writer(securityUtil.getCurrentUser()) // ✅ 변경
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        post.incrementCommentCount(); // ✅ 댓글 수 증가
        postRepository.save(post);
    }

    /** 대댓글 등록 */
    public void createReply(Long commentId, ReplyCreateRequest request) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = Reply.builder()
                .comment(parent)
                .writer(securityUtil.getCurrentUser()) // ✅ 변경
                .post(parent.getPost())
                .content(request.getContent())
                .build();

        replyRepository.save(reply);

        Post post = parent.getPost();
        post.incrementCommentCount();
        postRepository.save(post);
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

        Post post = comment.getPost();
        post.decrementCommentCount();
        postRepository.save(post);
    }

    /** 대댓글 삭제 */
    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        reply.softDelete();

        Post post = reply.getPost();
        post.decrementCommentCount();
        postRepository.save(post);
    }

    /** 게시글 기준 전체 댓글 및 대댓글 계층 조회 */
    public CommentListResponse getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Long userId = securityUtil.getCurrentUserId(); // ✅ 변경

        List<Comment> comments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post);
        Map<Long, List<Reply>> replyMap = replyRepository.findByPostAndIsDeletedFalse(post).stream()
                .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentResponse> result = comments.stream()
                .map(c -> CommentResponse.from(c, replyMap.getOrDefault(c.getId(), List.of()), userId))
                .collect(Collectors.toList());

        return new CommentListResponse(result);
    }
}
