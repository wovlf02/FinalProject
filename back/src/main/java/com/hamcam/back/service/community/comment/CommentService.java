package com.hamcam.back.service.community.comment;

import com.hamcam.back.dto.community.comment.request.CommentCreateRequest;
import com.hamcam.back.dto.community.comment.request.CommentUpdateRequest;
import com.hamcam.back.dto.community.comment.response.CommentListResponse;
import com.hamcam.back.dto.community.comment.response.CommentResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.*;
import com.hamcam.back.repository.community.attachment.AttachmentRepository;
import com.hamcam.back.repository.community.block.BlockRepository;
import com.hamcam.back.repository.community.comment.CommentRepository;
import com.hamcam.back.repository.community.comment.ReplyRepository;
import com.hamcam.back.repository.community.like.LikeRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.community.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 댓글(Comment) 및 대댓글(Reply) 서비스
 * <p>
 * 생성, 수정, 삭제, 계층형 조회, 좋아요, 신고, 차단 기능을 포함합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BlockRepository blockRepository;
    private final ReportRepository reportRepository;
    private final AttachmentRepository attachmentRepository;

    // ===== 인증 사용자 ID (mock) =====
    private Long getCurrentUserId() {
        return 1L;
    }

    // ===== 댓글 등록 =====

    public void createComment(Long postId, CommentCreateRequest request, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .writer(User.builder().id(getCurrentUserId()).build())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
        // (첨부파일 저장 로직은 AttachmentService 활용 or 추후 확장)
    }

    public void createReply(Long commentId, CommentCreateRequest request, MultipartFile[] files) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("상위 댓글이 존재하지 않습니다."));

        Reply reply = Reply.builder()
                .comment(parent)
                .writer(User.builder().id(getCurrentUserId()).build())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        replyRepository.save(reply);
    }

    // ===== 댓글/대댓글 수정 =====

    public void updateComment(Long commentId, CommentUpdateRequest request, MultipartFile[] files) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setContent(request.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
            return;
        }

        Optional<Reply> replyOpt = replyRepository.findById(commentId);
        if (replyOpt.isPresent()) {
            Reply reply = replyOpt.get();
            reply.setContent(request.getContent());
            reply.setUpdatedAt(LocalDateTime.now());
            replyRepository.save(reply);
            return;
        }

        throw new IllegalArgumentException("댓글 또는 대댓글이 존재하지 않습니다.");
    }

    // ===== 삭제 =====

    public void deleteComment(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else if (replyRepository.existsById(commentId)) {
            replyRepository.deleteById(commentId);
        } else {
            throw new IllegalArgumentException("삭제할 댓글 또는 대댓글이 존재하지 않습니다.");
        }
    }

    // ===== 계층형 조회 =====

    public CommentListResponse getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(
                postRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."))
        );

        Map<Long, List<Reply>> replyMap = replyRepository.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getComment().getId()));

        List<CommentResponse> result = comments.stream()
                .map(c -> CommentResponse.from(c, replyMap.getOrDefault(c.getId(), List.of())))
                .collect(Collectors.toList());

        return new CommentListResponse(result);
    }

    // ===== 좋아요 =====

    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        User user = User.builder().id(getCurrentUserId()).build();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        like -> {
                            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
                        },
                        () -> likeRepository.save(Like.builder().user(user).comment(comment).build())
                );
    }

    public void unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        User user = User.builder().id(getCurrentUserId()).build();

        likeRepository.findByUserAndComment(user, comment)
                .ifPresent(likeRepository::delete);
    }

    // ===== 신고 =====

    public void reportComment(Long commentId, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        User user = User.builder().id(getCurrentUserId()).build();

        boolean alreadyReported = reportRepository.findByReporterAndComment(user, comment).isPresent();
        if (alreadyReported) throw new IllegalArgumentException("이미 신고한 댓글입니다.");

        reportRepository.save(Report.builder()
                .reporter(user)
                .comment(comment)
                .reason(reason)
                .status("PENDING")
                .reportedAt(LocalDateTime.now())
                .build());
    }

    // ===== 차단/해제 =====

    public void blockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        b -> {
                            throw new IllegalArgumentException("이미 차단한 댓글입니다.");
                        },
                        () -> blockRepository.save(Block.builder().user(user).comment(comment).build())
                );
    }

    public void unblockComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = User.builder().id(getCurrentUserId()).build();

        blockRepository.findByUserAndComment(user, comment)
                .ifPresent(blockRepository::delete);
    }

    // ===== 차단 목록 조회 =====

    public List<CommentResponse> getBlockedComments() {
        User user = User.builder().id(getCurrentUserId()).build();
        List<Block> blocks = blockRepository.findByUserAndCommentIsNotNull(user);

        return blocks.stream()
                .map(b -> CommentResponse.from(b.getComment(), replyRepository.findByCommentId(b.getComment().getId())))
                .collect(Collectors.toList());
    }
}
