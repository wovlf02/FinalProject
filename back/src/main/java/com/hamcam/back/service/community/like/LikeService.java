package com.hamcam.back.service.community.like;

import com.hamcam.back.dto.community.like.response.LikeCountResponse;
import com.hamcam.back.dto.community.like.response.LikeStatusResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
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
        return 1L; // 향후 SecurityContextHolder 로 대체
    }

    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    // ====================== POST ======================

    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndPost(user, post).ifPresentOrElse(
                like -> {}, // 이미 좋아요한 상태면 무시
                () -> {
                    Like like = Like.builder().user(user).post(post).build();
                    likeRepository.save(like);
                }
        );
    }

    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndPost(user, post)
                .ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getPostLikeCount(Long postId) {
        return new LikeCountResponse(likeRepository.countByPostId(postId));
    }

    public LikeStatusResponse hasLikedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndPost(getCurrentUser(), post).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== COMMENT ======================

    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndComment(user, comment).ifPresentOrElse(
                like -> {}, // 이미 좋아요한 상태면 무시
                () -> {
                    Like like = Like.builder().user(user).comment(comment).build();
                    likeRepository.save(like);
                }
        );
    }

    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getCommentLikeCount(Long commentId) {
        return new LikeCountResponse(likeRepository.countByCommentId(commentId));
    }

    public LikeStatusResponse hasLikedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndComment(getCurrentUser(), comment).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== REPLY ======================

    public void likeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndReply(user, reply).ifPresentOrElse(
                like -> {}, // 이미 좋아요한 상태면 무시
                () -> {
                    Like like = Like.builder().user(user).reply(reply).build();
                    likeRepository.save(like);
                }
        );
    }

    public void unlikeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndReply(user, reply)
                .ifPresent(likeRepository::delete);
    }

    public LikeCountResponse getReplyLikeCount(Long replyId) {
        return new LikeCountResponse(likeRepository.countByReplyId(replyId));
    }

    public LikeStatusResponse hasLikedReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndReply(getCurrentUser(), reply).isPresent();
        return new LikeStatusResponse(liked);
    }
}
