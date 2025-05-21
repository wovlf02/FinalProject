package com.hamcam.back.service.community.like;

import com.hamcam.back.dto.community.like.response.LikeCountResponse;
import com.hamcam.back.dto.community.like.response.LikeStatusResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Like;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
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
    private final UserRepository userRepository;

    public boolean togglePostLike(Long postId, Long userId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        return likeRepository.findByUserAndPost(user, post)
                .map(existing -> {
                    likeRepository.delete(existing);
                    post.decrementLikeCount();
                    postRepository.save(post);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(Like.builder().user(user).post(post).build());
                    post.incrementLikeCount();
                    postRepository.save(post);
                    return true;
                });
    }

    public LikeCountResponse getPostLikeCount(Long postId) {
        return new LikeCountResponse(likeRepository.countByPostId(postId));
    }

    public LikeStatusResponse hasLikedPost(Long postId, Long userId) {
        User user = getUser(userId);
        Post post = getPost(postId);
        boolean liked = likeRepository.findByUserAndPost(user, post).isPresent();
        return new LikeStatusResponse(liked);
    }

    public boolean toggleCommentLike(Long commentId, Long userId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        return likeRepository.findByUserAndComment(user, comment)
                .map(existing -> {
                    likeRepository.delete(existing);
                    comment.decreaseLikeCount();
                    commentRepository.save(comment);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(Like.builder().user(user).comment(comment).build());
                    comment.increaseLikeCount();
                    commentRepository.save(comment);
                    return true;
                });
    }

    public LikeCountResponse getCommentLikeCount(Long commentId) {
        return new LikeCountResponse(likeRepository.countByCommentId(commentId));
    }

    public LikeStatusResponse hasLikedComment(Long commentId, Long userId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);
        boolean liked = likeRepository.findByUserAndComment(user, comment).isPresent();
        return new LikeStatusResponse(liked);
    }

    public boolean toggleReplyLike(Long replyId, Long userId) {
        User user = getUser(userId);
        Reply reply = getReply(replyId);

        return likeRepository.findByUserAndReply(user, reply)
                .map(existing -> {
                    likeRepository.delete(existing);
                    reply.decreaseLikeCount();
                    replyRepository.save(reply);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(Like.builder().user(user).reply(reply).build());
                    reply.increaseLikeCount();
                    replyRepository.save(reply);
                    return true;
                });
    }

    public LikeCountResponse getReplyLikeCount(Long replyId) {
        return new LikeCountResponse(likeRepository.countByReplyId(replyId));
    }

    public LikeStatusResponse hasLikedReply(Long replyId, Long userId) {
        User user = getUser(userId);
        Reply reply = getReply(replyId);
        boolean liked = likeRepository.findByUserAndReply(user, reply).isPresent();
        return new LikeStatusResponse(liked);
    }

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

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
