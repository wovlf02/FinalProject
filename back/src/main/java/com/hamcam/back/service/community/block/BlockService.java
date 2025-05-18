package com.hamcam.back.service.community.block;

import com.hamcam.back.dto.community.block.response.BlockedCommentListResponse;
import com.hamcam.back.dto.community.block.response.BlockedPostListResponse;
import com.hamcam.back.dto.community.block.response.BlockedReplyListResponse;
import com.hamcam.back.dto.community.block.response.BlockedTargetResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Block;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.block.BlockRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // ================== 게시글 ==================

    public void blockPost(Long postId) {
        User user = securityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Block block = blockRepository.findByUserAndPost(user, post)
                .orElse(Block.builder().user(user).post(post).build());

        if (block.isDeleted()) {
            block.restore();
            blockRepository.save(block);
        }
    }

    public void unblockPost(Long postId) {
        User user = securityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        blockRepository.findByUserAndPost(user, post)
                .ifPresent(block -> {
                    if (!block.isDeleted()) {
                        block.softDelete();
                        blockRepository.save(block);
                    }
                });
    }

    public BlockedPostListResponse getBlockedPosts() {
        User user = securityUtil.getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNullAndIsDeletedFalse(user);

        return new BlockedPostListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getPost().getId(), "POST"))
                .collect(Collectors.toList()));
    }

    // ================== 댓글 ==================

    public void blockComment(Long commentId) {
        User user = securityUtil.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Block block = blockRepository.findByUserAndComment(user, comment)
                .orElse(Block.builder().user(user).comment(comment).build());

        if (block.isDeleted()) {
            block.restore();
            blockRepository.save(block);
        }
    }

    public void unblockComment(Long commentId) {
        User user = securityUtil.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(block -> {
                    if (!block.isDeleted()) {
                        block.softDelete();
                        blockRepository.save(block);
                    }
                });
    }

    public BlockedCommentListResponse getBlockedComments() {
        User user = securityUtil.getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNullAndIsDeletedFalse(user);

        return new BlockedCommentListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getComment().getId(), "COMMENT"))
                .collect(Collectors.toList()));
    }

    // ================== 대댓글 ==================

    public void blockReply(Long replyId) {
        User user = securityUtil.getCurrentUser();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        Block block = blockRepository.findByUserAndReply(user, reply)
                .orElse(Block.builder().user(user).reply(reply).build());

        if (block.isDeleted()) {
            block.restore();
            blockRepository.save(block);
        }
    }

    public void unblockReply(Long replyId) {
        User user = securityUtil.getCurrentUser();
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        blockRepository.findByUserAndReply(user, reply)
                .ifPresent(block -> {
                    if (!block.isDeleted()) {
                        block.softDelete();
                        blockRepository.save(block);
                    }
                });
    }

    public BlockedReplyListResponse getBlockedReplies() {
        User user = securityUtil.getCurrentUser();
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNullAndIsDeletedFalse(user);

        return new BlockedReplyListResponse(blocks.stream()
                .map(block -> new BlockedTargetResponse(block.getReply().getId(), "REPLY"))
                .collect(Collectors.toList()));
    }
}
