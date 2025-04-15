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

/**
 * 좋아요 기능 서비스 클래스
 * 게시글, 댓글, 대댓글에 대한 좋아요 등록/취소/조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // ====================== USER MOCK ======================

    /**
     * 현재 로그인된 사용자 ID 조회 (Mock)
     * 추후 Spring Security 연동 시 SecurityContextHolder로 대체 필요
     */
    private Long getCurrentUserId() {
        return 1L;
    }

    /**
     * 현재 로그인된 사용자 객체 생성 (Mock)
     */
    private User getCurrentUser() {
        return User.builder().id(getCurrentUserId()).build();
    }

    // ====================== POST ======================

    /**
     * 게시글에 좋아요를 추가합니다.
     * 이미 좋아요한 경우 중복 등록을 방지합니다.
     *
     * @param postId 대상 게시글 ID
     */
    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndPost(user, post).ifPresentOrElse(
                like -> {}, // 이미 좋아요한 경우 무시
                () -> likeRepository.save(Like.builder().user(user).post(post).build())
        );
    }

    /**
     * 게시글 좋아요를 취소합니다.
     *
     * @param postId 대상 게시글 ID
     */
    public void unlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndPost(user, post)
                .ifPresent(likeRepository::delete);
    }

    /**
     * 게시글의 좋아요 수를 조회합니다.
     *
     * @param postId 대상 게시글 ID
     * @return LikeCountResponse (like count 포함)
     */
    public LikeCountResponse getPostLikeCount(Long postId) {
        return new LikeCountResponse(likeRepository.countByPostId(postId));
    }

    /**
     * 현재 사용자가 해당 게시글에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param postId 대상 게시글 ID
     * @return LikeStatusResponse (liked 여부)
     */
    public LikeStatusResponse hasLikedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndPost(getCurrentUser(), post).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== COMMENT ======================

    /**
     * 댓글에 좋아요를 추가합니다.
     * 이미 좋아요한 경우 무시됩니다.
     *
     * @param commentId 대상 댓글 ID
     */
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndComment(user, comment).ifPresentOrElse(
                like -> {},
                () -> likeRepository.save(Like.builder().user(user).comment(comment).build())
        );
    }

    /**
     * 댓글 좋아요를 취소합니다.
     *
     * @param commentId 대상 댓글 ID
     */
    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(likeRepository::delete);
    }

    /**
     * 댓글의 좋아요 수를 조회합니다.
     *
     * @param commentId 대상 댓글 ID
     * @return LikeCountResponse (like count 포함)
     */
    public LikeCountResponse getCommentLikeCount(Long commentId) {
        return new LikeCountResponse(likeRepository.countByCommentId(commentId));
    }

    /**
     * 현재 사용자가 해당 댓글에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param commentId 대상 댓글 ID
     * @return LikeStatusResponse (liked 여부)
     */
    public LikeStatusResponse hasLikedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndComment(getCurrentUser(), comment).isPresent();
        return new LikeStatusResponse(liked);
    }

    // ====================== REPLY ======================

    /**
     * 대댓글에 좋아요를 추가합니다.
     * 이미 좋아요한 경우 중복 등록 방지
     *
     * @param replyId 대상 대댓글 ID
     */
    public void likeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndReply(user, reply).ifPresentOrElse(
                like -> {},
                () -> likeRepository.save(Like.builder().user(user).reply(reply).build())
        );
    }

    /**
     * 대댓글 좋아요를 취소합니다.
     *
     * @param replyId 대상 대댓글 ID
     */
    public void unlikeReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        User user = getCurrentUser();

        likeRepository.findByUserAndReply(user, reply)
                .ifPresent(likeRepository::delete);
    }

    /**
     * 대댓글의 좋아요 수를 조회합니다.
     *
     * @param replyId 대상 대댓글 ID
     * @return LikeCountResponse (like count 포함)
     */
    public LikeCountResponse getReplyLikeCount(Long replyId) {
        return new LikeCountResponse(likeRepository.countByReplyId(replyId));
    }

    /**
     * 현재 사용자가 해당 대댓글에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param replyId 대상 대댓글 ID
     * @return LikeStatusResponse (liked 여부)
     */
    public LikeStatusResponse hasLikedReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글이 존재하지 않습니다."));
        boolean liked = likeRepository.findByUserAndReply(getCurrentUser(), reply).isPresent();
        return new LikeStatusResponse(liked);
    }
}
