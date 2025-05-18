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
 * [BlockRepository]
 *
 * 게시글/댓글/대댓글 차단(Block) 정보를 관리하는 JPA Repository입니다.
 * - 차단 등록 여부 확인
 * - 차단한 항목 목록 조회
 * - 삭제되지 않은(active) 차단 필터링 지원
 */
public interface BlockRepository extends JpaRepository<Block, Long> {

    // ===== 전체 차단 목록 조회 (삭제 여부 관계없이) =====

    /**
     * [사용자가 차단한 게시글 목록]
     *
     * @param user 차단한 사용자
     * @return 차단된 게시글 Block 목록
     */
    List<Block> findByUserAndPostIsNotNull(User user);

    /**
     * [사용자가 차단한 댓글 목록]
     *
     * @param user 차단한 사용자
     * @return 차단된 댓글 Block 목록
     */
    List<Block> findByUserAndCommentIsNotNull(User user);

    /**
     * [사용자가 차단한 대댓글 목록]
     *
     * @param user 차단한 사용자
     * @return 차단된 대댓글 Block 목록
     */
    List<Block> findByUserAndReplyIsNotNull(User user);


    // ===== 차단 여부 확인 =====

    /**
     * [특정 게시글 차단 여부 조회]
     *
     * @param user 차단한 사용자
     * @param post 차단 대상 게시글
     * @return 차단 정보 (Optional)
     */
    Optional<Block> findByUserAndPost(User user, Post post);

    /**
     * [특정 댓글 차단 여부 조회]
     *
     * @param user 차단한 사용자
     * @param comment 차단 대상 댓글
     * @return 차단 정보 (Optional)
     */
    Optional<Block> findByUserAndComment(User user, Comment comment);

    /**
     * [특정 대댓글 차단 여부 조회]
     *
     * @param user 차단한 사용자
     * @param reply 차단 대상 대댓글
     * @return 차단 정보 (Optional)
     */
    Optional<Block> findByUserAndReply(User user, Reply reply);


    // ===== 삭제되지 않은 차단 목록 조회 (isDeleted = false) =====

    /**
     * [삭제되지 않은 게시글 차단 목록]
     */
    List<Block> findByUserAndPostIsNotNullAndIsDeletedFalse(User user);

    /**
     * [삭제되지 않은 댓글 차단 목록]
     */
    List<Block> findByUserAndCommentIsNotNullAndIsDeletedFalse(User user);

    /**
     * [삭제되지 않은 대댓글 차단 목록]
     */
    List<Block> findByUserAndReplyIsNotNullAndIsDeletedFalse(User user);
}
