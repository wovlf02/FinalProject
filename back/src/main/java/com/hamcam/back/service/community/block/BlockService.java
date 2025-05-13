package com.hamcam.back.service.community.block;

import com.hamcam.back.dto.community.block.response.*;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.block.BlockRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.hamcam.back.global.security.SecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new CustomException("사용자 정보를 불러올 수 없습니다.");
    }

    private User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자 정보를 불러올 수 없습니다."));
    }

    // ================== 게시글 ==================
    public void blockPost(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Block block = blockRepository.findByUserAndPost(user, post)
                .orElse(Block.builder().user(user).post(post).build());

        block.restore();
        blockRepository.save(block);
    }

    public void unblockPost(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        blockRepository.findByUserAndPost(user, post)
                .ifPresent(block -> {
                    block.softDelete();
                    blockRepository.save(block);
                });
    }

    public BlockedPostListResponse getBlockedPosts() {
        User user = getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNullAndIsDeletedFalse(user);
        return new BlockedPostListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getPost().getId(), "POST"))
                .collect(Collectors.toList()));
    }

    // ================== 댓글 ==================
    public void blockComment(Long commentId) {
        User user = getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Block block = blockRepository.findByUserAndComment(user, comment)
                .orElse(Block.builder().user(user).comment(comment).build());

        block.restore();
        blockRepository.save(block);
    }

    public void unblockComment(Long commentId) {
        User user = getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(block -> {
                    block.softDelete();
                    blockRepository.save(block);
                });
    }

    public BlockedCommentListResponse getBlockedComments() {
        User user = getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNullAndIsDeletedFalse(user);
        return new BlockedCommentListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getComment().getId(), "COMMENT"))
                .collect(Collectors.toList()));
    }

    // ================== 대댓글 ==================
    public void blockReply(Long replyId) {
        User user = getCurrentUser();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        Block block = blockRepository.findByUserAndReply(user, reply)
                .orElse(Block.builder().user(user).reply(reply).build());

        block.restore();
        blockRepository.save(block);
    }

    public void unblockReply(Long replyId) {
        User user = getCurrentUser();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        blockRepository.findByUserAndReply(user, reply)
                .ifPresent(block -> {
                    block.softDelete();
                    blockRepository.save(block);
                });
    }

    public BlockedReplyListResponse getBlockedReplies() {
        User user = getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNullAndIsDeletedFalse(user);
        return new BlockedReplyListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getReply().getId(), "REPLY"))
                .collect(Collectors.toList()));
    }
}
