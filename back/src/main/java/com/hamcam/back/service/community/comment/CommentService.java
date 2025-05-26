package com.hamcam.back.service.community.comment;

import com.hamcam.back.dto.community.comment.request.*;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final UserRepository userRepository;

    /** ✅ 댓글 등록 */
    public Long createComment(CommentCreateRequest request, HttpServletRequest httpRequest) {
        Post post = getPost(request.getPostId());
        User user = getSessionUser(httpRequest);

        Comment comment = Comment.builder()
                .post(post)
                .writer(user)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        post.incrementCommentCount();
        return saved.getId();
    }

    /** ✅ 대댓글 등록 */
    public Long createReply(ReplyCreateRequest request, HttpServletRequest httpRequest) {
        Comment parent = getComment(request.getCommentId());
        User user = getSessionUser(httpRequest);

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

    /** ✅ 댓글 수정 */
    public void updateComment(CommentUpdateRequest request, HttpServletRequest httpRequest) {
        Comment comment = getComment(request.getCommentId());
        validateUser(comment.getWriter(), httpRequest);
        comment.updateContent(request.getContent());
    }

    /** ✅ 대댓글 수정 */
    public void updateReply(ReplyUpdateRequest request, HttpServletRequest httpRequest) {
        Reply reply = getReply(request.getReplyId());
        validateUser(reply.getWriter(), httpRequest);
        reply.updateContent(request.getContent());
    }

    /** ✅ 댓글 삭제 (하드 삭제 방식) */
    public void deleteComment(CommentDeleteRequest request, HttpServletRequest httpRequest) {
        Comment comment = getComment(request.getCommentId());
        validateUser(comment.getWriter(), httpRequest);

        // 댓글에 연결된 대댓글도 함께 삭제
        List<Reply> replies = replyRepository.findByComment(comment);
        replyRepository.deleteAll(replies);

        comment.getPost().decrementCommentCount();
        commentRepository.delete(comment);
    }

    /** ✅ 대댓글 삭제 (하드 삭제 방식) */
    public void deleteReply(ReplyDeleteRequest request, HttpServletRequest httpRequest) {
        Reply reply = getReply(request.getReplyId());
        validateUser(reply.getWriter(), httpRequest);

        reply.getPost().decrementCommentCount();
        replyRepository.delete(reply);
    }

    /** ✅ 게시글 기준 전체 댓글 + 대댓글 조회 */
    public CommentListResponse getCommentsByPost(CommentListRequest request, HttpServletRequest httpRequest) {
        Post post = getPost(request.getPostId());
        Long sessionUserId = SessionUtil.getUserId(httpRequest);

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        Map<Long, List<Reply>> replyMap = replyRepository.findByPost(post).stream()
                .collect(Collectors.groupingBy(reply -> reply.getComment().getId()));

        List<CommentResponse> responseList = comments.stream()
                .map(comment -> CommentResponse.from(comment,
                        replyMap.getOrDefault(comment.getId(), List.of()),
                        sessionUserId))
                .collect(Collectors.toList());

        return new CommentListResponse(responseList);
    }

    // ===== 내부 유틸 =====

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
    }

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateUser(User writer, HttpServletRequest request) {
        Long sessionUserId = SessionUtil.getUserId(request);
        if (!writer.getId().equals(sessionUserId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }
}
