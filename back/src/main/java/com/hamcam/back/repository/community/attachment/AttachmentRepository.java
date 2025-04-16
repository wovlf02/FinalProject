package com.hamcam.back.repository.community.attachment;

import com.hamcam.back.entity.community.Attachment;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 첨부파일(Attachment) 관련 JPA Repository
 * <p>
 * 게시글, 댓글, 대댓글에 첨부된 파일을 조회하거나 삭제할 때 사용됩니다.
 * </p>
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * 게시글에 첨부된 파일 목록 조회
     */
    List<Attachment> findByPost(Post post);

    /**
     * 댓글에 첨부된 파일 목록 조회
     */
    List<Attachment> findByComment(Comment comment);

    /**
     * 대댓글에 첨부된 파일 목록 조회
     */
    List<Attachment> findByReply(Reply reply);

    /**
     * 게시글 ID로 첨부파일 조회
     */
    List<Attachment> findByPostId(Long postId);

    /**
     * 댓글 ID로 첨부파일 조회
     */
    List<Attachment> findByCommentId(Long commentId);

    /**
     * 대댓글 ID로 첨부파일 조회
     */
    List<Attachment> findByReplyId(Long replyId);
}
