package com.hamcam.back.service.community.block;

import com.hamcam.back.dto.community.block.request.BlockTargetRequest;
import com.hamcam.back.dto.community.block.request.UnblockTargetRequest;
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
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public void blockPost(BlockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPost(request.getTargetId());
        block(user, post);
    }

    public void unblockPost(UnblockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Post post = getPost(request.getTargetId());
        unblock(user, post);
    }

    public BlockedPostListResponse getBlockedPosts(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNull(user);
        return new BlockedPostListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getPost().getId(), "POST"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 댓글 차단 =====
    public void blockComment(BlockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Comment comment = getComment(request.getTargetId());
        block(user, comment);
    }

    public void unblockComment(UnblockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Comment comment = getComment(request.getTargetId());
        unblock(user, comment);
    }

    public BlockedCommentListResponse getBlockedComments(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNull(user);
        return new BlockedCommentListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getComment().getId(), "COMMENT"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 대댓글 차단 =====
    public void blockReply(BlockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Reply reply = getReply(request.getTargetId());
        block(user, reply);
    }

    public void unblockReply(UnblockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Reply reply = getReply(request.getTargetId());
        unblock(user, reply);
    }

    public BlockedReplyListResponse getBlockedReplies(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNull(user);
        return new BlockedReplyListResponse(
                blocks.stream()
                        .map(b -> new BlockedTargetResponse(b.getReply().getId(), "REPLY"))
                        .collect(Collectors.toList())
        );
    }

    // ===== 사용자 차단 =====
    public void blockUser(BlockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        User target = getUser(request.getTargetId());
        blockUserInternal(user, target);
    }

    public void unblockUser(UnblockTargetRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        User target = getUser(request.getTargetId());
        unblockUserInternal(user, target);
    }

    public BlockedUserListResponse getBlockedUsers(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<Block> blocks = blockRepository.findByUserAndBlockedUserIsNotNull(user);
        return new BlockedUserListResponse(
                blocks.stream()
                        .map(b -> BlockedUserListResponse.BlockedUserDto.from(b.getBlockedUser()))
                        .collect(Collectors.toList())
        );
    }

    // ===== 내부 공통 처리 =====
    private void block(User user, Object target) {
        if (findBlock(user, target).isEmpty()) {
            Block block = createBlock(user, target);
            blockRepository.save(block);
        }
    }

    private void unblock(User user, Object target) {
        findBlock(user, target).ifPresent(blockRepository::delete);
    }

    private void blockUserInternal(User user, User target) {
        if (blockRepository.findByUserAndBlockedUser(user, target).isEmpty()) {
            Block block = Block.builder().user(user).blockedUser(target).build();
            blockRepository.save(block);
        }
    }

    private void unblockUserInternal(User user, User target) {
        blockRepository.findByUserAndBlockedUser(user, target)
                .ifPresent(blockRepository::delete);
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

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return getUser(userId);
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
