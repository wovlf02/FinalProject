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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    // 프로토타입: userId를 파라미터로 받음
    // ================== 게시글 ==================

    public void blockPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        block(userId, post);
    }

    public void unblockPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        unblock(userId, post);
    }

    public BlockedPostListResponse getBlockedPosts(Long userId) {
        User user = getUser(userId);
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNullAndIsDeletedFalse(user);
        return new BlockedPostListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getPost().getId(), "POST"))
                        .collect(Collectors.toList())
        );
    }

    // ================== 댓글 ==================

    public void blockComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        block(userId, comment);
    }

    public void unblockComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        unblock(userId, comment);
    }

    public BlockedCommentListResponse getBlockedComments(Long userId) {
        User user = getUser(userId);
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNullAndIsDeletedFalse(user);
        return new BlockedCommentListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getComment().getId(), "COMMENT"))
                        .collect(Collectors.toList())
        );
    }

    // ================== 대댓글 ==================

    public void blockReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        block(userId, reply);
    }

    public void unblockReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        unblock(userId, reply);
    }

    public BlockedReplyListResponse getBlockedReplies(Long userId) {
        User user = getUser(userId);
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNullAndIsDeletedFalse(user);
        return new BlockedReplyListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getReply().getId(), "REPLY"))
                        .collect(Collectors.toList())
        );
    }

    // ================== 공통 블록 로직 ==================

    private void block(Long userId, Object target) {
        User user = getUser(userId);

        Block block = findBlock(user, target).orElseGet(() -> createBlock(user, target));

        if (block.isDeleted()) {
            block.restore();
            blockRepository.save(block);
        }
    }

    private void unblock(Long userId, Object target) {
        User user = getUser(userId);

        findBlock(user, target).ifPresent(block -> {
            if (!block.isDeleted()) {
                block.softDelete();
                blockRepository.save(block);
            }
        });
    }

    private Optional<Block> findBlock(User user, Object target) {
        if (target instanceof Post post) {
            return blockRepository.findByUserAndPost(user, post);
        } else if (target instanceof Comment comment) {
            return blockRepository.findByUserAndComment(user, comment);
        } else if (target instanceof Reply reply) {
            return blockRepository.findByUserAndReply(user, reply);
        }
        return Optional.empty();
    }

    private Block createBlock(User user, Object target) {
        if (target instanceof Post post) {
            return Block.builder().user(user).post(post).build();
        } else if (target instanceof Comment comment) {
            return Block.builder().user(user).comment(comment).build();
        } else if (target instanceof Reply reply) {
            return Block.builder().user(user).reply(reply).build();
        }
        throw new CustomException(ErrorCode.INVALID_INPUT);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}