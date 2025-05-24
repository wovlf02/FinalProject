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
 * 게시글, 댓글, 대댓글, 사용자(User)에 대한 차단(Block) 정보를 관리하는 JPA Repository입니다.
 * - 차단 등록 여부 확인
 * - 차단한 항목 목록 조회
 * - 삭제되지 않은(active) 차단 필터링 지원
 */
public interface BlockRepository extends JpaRepository<Block, Long> {

    // ===== 전체 차단 목록 조회 (삭제 여부 관계없이) =====

    List<Block> findByUserAndPostIsNotNull(User user);
    List<Block> findByUserAndCommentIsNotNull(User user);
    List<Block> findByUserAndReplyIsNotNull(User user);
    List<Block> findByUserAndBlockedUserIsNotNull(User user);

    // ===== 삭제되지 않은 차단 목록 조회 (isDeleted = false) =====

    List<Block> findByUserAndPostIsNotNullAndIsDeletedFalse(User user);
    List<Block> findByUserAndCommentIsNotNullAndIsDeletedFalse(User user);
    List<Block> findByUserAndReplyIsNotNullAndIsDeletedFalse(User user);
    List<Block> findByUserAndBlockedUserIsNotNullAndIsDeletedFalse(User user);

    // ===== 차단 여부 확인 =====

    Optional<Block> findByUserAndPost(User user, Post post);
    Optional<Block> findByUserAndComment(User user, Comment comment);
    Optional<Block> findByUserAndReply(User user, Reply reply);
    Optional<Block> findByUserAndBlockedUser(User user, User blockedUser);
}
