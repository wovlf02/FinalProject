package com.hamcam.back.service.community.block;

import com.hamcam.back.dto.community.block.request.BlockTargetRequest;
import com.hamcam.back.dto.community.block.request.UnblockTargetRequest;
import com.hamcam.back.dto.community.block.request.UserOnlyRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    // ===== 게시글 차단 =====
    public void blockPost(BlockTargetRequest request) {
        Post post = getPost(request.getTargetId());
        block(request.getUserId(), post);
    }

    public void unblockPost(UnblockTargetRequest request) {
        Post post = getPost(request.getTargetId());
        unblock(request.getUserId(), post);
    }

    public BlockedPostListResponse getBlockedPosts(UserOnlyRequest request) {
        User user = getUser(request.getUserId());
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNullAndIsDeletedFalse(user);
        return new BlockedPostListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getPost().getId(), "POST"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 댓글 차단 =====
    public void blockComment(BlockTargetRequest request) {
        Comment comment = getComment(request.getTargetId());
        block(request.getUserId(), comment);
    }

    public void unblockComment(UnblockTargetRequest request) {
        Comment comment = getComment(request.getTargetId());
        unblock(request.getUserId(), comment);
    }

    public BlockedCommentListResponse getBlockedComments(UserOnlyRequest request) {
        User user = getUser(request.getUserId());
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNullAndIsDeletedFalse(user);
        return new BlockedCommentListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getComment().getId(), "COMMENT"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 대댓글 차단 =====
    public void blockReply(BlockTargetRequest request) {
        Reply reply = getReply(request.getTargetId());
        block(request.getUserId(), reply);
    }

    public void unblockReply(UnblockTargetRequest request) {
        Reply reply = getReply(request.getTargetId());
        unblock(request.getUserId(), reply);
    }

    public BlockedReplyListResponse getBlockedReplies(UserOnlyRequest request) {
        User user = getUser(request.getUserId());
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNullAndIsDeletedFalse(user);
        return new BlockedReplyListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getReply().getId(), "REPLY"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 사용자 차단 =====
    public void blockUser(BlockTargetRequest request) {
        User target = getUser(request.getTargetId());
        blockUserInternal(request.getUserId(), target);
    }

    public void unblockUser(UnblockTargetRequest request) {
        User target = getUser(request.getTargetId());
        unblockUserInternal(request.getUserId(), target);
    }

    public BlockedUserListResponse getBlockedUsers(UserOnlyRequest request) {
        User user = getUser(request.getUserId());
        List<Block> blocks = blockRepository.findByUserAndBlockedUserIsNotNullAndIsDeletedFalse(user);
        return new BlockedUserListResponse(
                blocks.stream()
                        .map(b -> BlockedUserListResponse.BlockedUserDto.from(b.getBlockedUser()))
                        .collect(Collectors.toList())
        );
    }

    // ===== 내부 공통 처리 =====
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

    private void blockUserInternal(Long userId, User target) {
        User user = getUser(userId);
        Block block = blockRepository.findByUserAndBlockedUser(user, target)
                .orElseGet(() -> Block.builder().user(user).blockedUser(target).build());

        if (block.isDeleted()) {
            block.restore();
        }
        blockRepository.save(block);
    }

    private void unblockUserInternal(Long userId, User target) {
        User user = getUser(userId);
        blockRepository.findByUserAndBlockedUser(user, target).ifPresent(block -> {
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

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Reply getReply(Long id) {
        return replyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
    }
}
