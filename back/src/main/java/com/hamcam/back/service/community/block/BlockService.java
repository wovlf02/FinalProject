package com.hamcam.back.service.community.block;

import com.hamcam.back.dto.community.block.response.BlockedCommentListResponse;
import com.hamcam.back.dto.community.block.response.BlockedPostListResponse;
import com.hamcam.back.dto.community.block.response.BlockedReplyListResponse;
import com.hamcam.back.dto.community.block.response.BlockedTargetResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.repository.community.block.BlockRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 차단(Block) 서비스
 * <p>
 * 사용자가 게시글, 댓글, 대댓글을 차단하거나 차단을 해제하고,
 * 차단한 목록을 조회할 수 있도록 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // 인증된 사용자 ID (실제 서비스에서는 SecurityContextHolder로 대체)
    private Long getCurrentUserId() {
        return 1L; // mock user id
    }

    // ================== 게시글 차단 ==================

    public void blockPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        boolean alreadyBlocked = blockRepository.findByUserAndPost(user, post).isPresent();
        if (!alreadyBlocked) {
            Block block = Block.builder().user(user).post(post).build();
            blockRepository.save(block);
        }
    }

    public void unblockPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndPost(user, post)
                .ifPresent(blockRepository::delete);
    }

    public BlockedPostListResponse getBlockedPosts() {
        User user = User.builder().id(getCurrentUserId()).build();
        List<Block> blocks = blockRepository.findByUserAndPostIsNotNull(user);
        return new BlockedPostListResponse(
                blocks.stream()
                        .map(block -> BlockedTargetResponse.builder()
                                .targetId(block.getPost().getId())
                                .targetType("POST")
                                .build())
                        .collect(Collectors.toList())
        );
    }

    // ================== 댓글 차단 ==================

    public void blockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        boolean alreadyBlocked = blockRepository.findByUserAndComment(user, comment).isPresent();
        if (!alreadyBlocked) {
            Block block = Block.builder().user(user).comment(comment).build();
            blockRepository.save(block);
        }
    }

    public void unblockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(blockRepository::delete);
    }

    public BlockedCommentListResponse getBlockedComments() {
        User user = User.builder().id(getCurrentUserId()).build();
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNull(user);
        return new BlockedCommentListResponse(
                blocks.stream()
                        .map(block -> BlockedTargetResponse.builder()
                                .targetId(block.getComment().getId())
                                .targetType("COMMENT")
                                .build())
                        .collect(Collectors.toList())
        );
    }

    // ================== 대댓글 차단 ==================

    public void blockReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        boolean alreadyBlocked = blockRepository.findByUserAndReply(user, reply).isPresent();
        if (!alreadyBlocked) {
            Block block = Block.builder().user(user).reply(reply).build();
            blockRepository.save(block);
        }
    }

    public void unblockReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndReply(user, reply)
                .ifPresent(blockRepository::delete);
    }

    public BlockedReplyListResponse getBlockedReplies() {
        User user = User.builder().id(getCurrentUserId()).build();
        List<Block> blocks = blockRepository.findByUserAndReplyIsNotNull(user);
        return new BlockedReplyListResponse(
                blocks.stream()
                        .map(block -> BlockedTargetResponse.builder()
                                .targetId(block.getReply().getId())
                                .targetType("REPLY")
                                .build())
                        .collect(Collectors.toList())
        );
    }
}
