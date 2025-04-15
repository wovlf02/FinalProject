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
 * 사용자가 게시글, 댓글, 대댓글을 차단하거나 해제하며,
 * 본인이 차단한 콘텐츠 목록을 조회할 수 있도록 지원합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    /**
     * 현재 로그인된 사용자 ID 반환 (Mock)
     * @return 사용자 ID
     */
    private Long getCurrentUserId() {
        return 1L;
    }

    // ================== 게시글 차단 ==================

    /**
     * 게시글 차단
     * @param postId 차단할 게시글 ID
     */
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

    /**
     * 게시글 차단 해제
     * @param postId 차단 해제할 게시글 ID
     */
    public void unblockPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndPost(user, post)
                .ifPresent(blockRepository::delete);
    }

    /**
     * 차단한 게시글 목록 조회
     * @return BlockedPostListResponse
     */
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

    /**
     * 댓글 차단
     * @param commentId 차단할 댓글 ID
     */
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

    /**
     * 댓글 차단 해제
     * @param commentId 차단 해제할 댓글 ID
     */
    public void unblockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(blockRepository::delete);
    }

    /**
     * 차단한 댓글 목록 조회
     * @return BlockedCommentListResponse
     */
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

    /**
     * 대댓글 차단
     * @param replyId 차단할 대댓글 ID
     */
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

    /**
     * 대댓글 차단 해제
     * @param replyId 차단 해제할 대댓글 ID
     */
    public void unblockReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndReply(user, reply)
                .ifPresent(blockRepository::delete);
    }

    /**
     * 차단한 대댓글 목록 조회
     * @return BlockedReplyListResponse
     */
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
