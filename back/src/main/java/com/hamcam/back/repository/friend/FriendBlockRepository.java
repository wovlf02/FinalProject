package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 차단(FriendBlock) JPA Repository
 * <p>
 * 친구 관계 여부와 무관하게 사용자가 특정 사용자를 차단한 상태를 관리합니다.
 * 차단 여부 확인, 차단 해제, 차단 목록 조회에 사용됩니다.
 * </p>
 */
public interface FriendBlockRepository extends JpaRepository<FriendBlock, Long> {

    /**
     * 차단 여부 확인 (중복 차단 방지용)
     */
    Optional<FriendBlock> findByBlockerAndBlocked(User blocker, User blocked);

    /**
     * 내가 차단한 사용자 목록 조회
     */
    List<FriendBlock> findByBlocker(User blocker);

    /**
     * 나를 차단한 사용자 목록 조회
     */
    List<FriendBlock> findByBlocked(User blocked);

    /**
     * 특정 차단 내역 존재 여부 확인
     */
    boolean existsByBlockerAndBlocked(User blocker, User blocked);
}
