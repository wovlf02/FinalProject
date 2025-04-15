package com.hamcam.back.repository.community.block;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Block;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 차단(Block) 관련 JPA Repository
 * <p>
 * 사용자가 게시글, 댓글, 대댓글을 차단한 내역을 조회하거나 등록/삭제하는 데 사용됩니다.
 * </p>
 */
public interface BlockRepository extends JpaRepository<Block, Long> {

    /**
     * 사용자가 차단한 게시글 목록 조회
     */
    List<Block> findByUserAndPostIsNotNull(User user);

    /**
     * 사용자가 차단한 댓글 목록 조회
     */
    List<Block> findByUserAndCommentIsNotNull(User user);

    /**
     * 사용자가 차단한 대댓글 목록 조회
     */
    List<Block> findByUserAndReplyIsNotNull(User user);

    /**
     * 특정 게시글에 대한 차단 여부 조회
     */
    Optional<Block> findByUserAndPost(User user, Post post);

    /**
     * 특정 댓글에 대한 차단 여부 조회
     */
    Optional<Block> findByUserAndComment(User user, Comment comment);

    /**
     * 특정 대댓글에 대한 차단 여부 조회
     */
    Optional<Block> findByUserAndReply(User user, Reply reply);
}
