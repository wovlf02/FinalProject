package com.hamcam.back.service.community.like;

import com.hamcam.back.dto.community.like.response.LikeCountResponse;
import com.hamcam.back.dto.community.like.response.LikeStatusResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Like;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.like.LikeRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // ====================== USER MOCK ======================

    private Long getCurrentUserId() {
        return 1L; // TODO: Spring Security 적용 후 SecurityContextHolder 사용
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    // ====================== POST ======================

    public void likePost(Long postId) {
        Post post = getPost(postId);
        User user = getCurrentUser();

        if (likeRepository.findByUserAndPost(user, post).isEmpty()) {
            likeRepository.save(Like.builder().user(user).post(post).build());
        }
    }

    public void unlikePost(Long postId) {
        Post post = getPost(postId);
        User user = getCurrentUser();

        likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getPostLikeCount(Long postId) {
        return new LikeCountResponse(likeRepository.countByPostId(postId));
    }

    public LikeStatusResponse hasLikedPost(Long postId) {
        Post post = getPost(postId);
        boolean liked = likeRepository.findByUserAndPost(getCurrentUser(), post).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== COMMENT ======================

    public void likeComment(Long commentId) {
        Comment comment = getComment(commentId);
        User user = getCurrentUser();

        if (likeRepository.findByUserAndComment(user, comment).isEmpty()) {
            likeRepository.save(Like.builder().user(user).comment(comment).build());
        }
    }

    public void unlikeComment(Long commentId) {
        Comment comment = getComment(commentId);
        User user = getCurrentUser();

        likeRepository.findByUserAndComment(user, comment).ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getCommentLikeCount(Long commentId) {
        return new LikeCountResponse(likeRepository.countByCommentId(commentId));
    }

    public LikeStatusResponse hasLikedComment(Long commentId) {
        Comment comment = getComment(commentId);
        boolean liked = likeRepository.findByUserAndComment(getCurrentUser(), comment).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== REPLY ======================

    public void likeReply(Long replyId) {
        Reply reply = getReply(replyId);
        User user = getCurrentUser();

        if (likeRepository.findByUserAndReply(user, reply).isEmpty()) {
            likeRepository.save(Like.builder().user(user).reply(reply).build());
        }
    }

    public void unlikeReply(Long replyId) {
        Reply reply = getReply(replyId);
        User user = getCurrentUser();

        likeRepository.findByUserAndReply(user, reply).ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getReplyLikeCount(Long replyId) {
        return new LikeCountResponse(likeRepository.countByReplyId(replyId));
    }

    public LikeStatusResponse hasLikedReply(Long replyId) {
        Reply reply = getReply(replyId);
        boolean liked = likeRepository.findByUserAndReply(getCurrentUser(), reply).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== PRIVATE HELPERS ======================

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));
    }
}
