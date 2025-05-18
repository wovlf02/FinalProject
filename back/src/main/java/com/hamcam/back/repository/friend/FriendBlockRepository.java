package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [FriendBlockRepository]
 *
 * 사용자 차단(FriendBlock) 정보를 관리하는 JPA Repository입니다.
 * - 친구 관계와 무관하게 사용자 간 차단 상태를 저장합니다.
 * - 차단 여부 확인, 해제, 목록 조회에 활용됩니다.
 */
public interface FriendBlockRepository extends JpaRepository<FriendBlock, Long> {

    /**
     * [차단 여부 확인]
     * 특정 사용자가 다른 사용자를 차단한 상태인지 확인합니다.
     * 중복 차단을 방지하기 위해 사용됩니다.
     *
     * @param blocker 차단한 사용자
     * @param blocked 차단당한 사용자
     * @return 차단 정보 (Optional)
     */
    Optional<FriendBlock> findByBlockerAndBlocked(User blocker, User blocked);

    /**
     * [내가 차단한 사용자 목록 조회]
     * 현재 로그인한 사용자가 차단한 사용자 목록을 조회합니다.
     *
     * @param blocker 차단한 사용자
     * @return 내가 차단한 사용자 리스트
     */
    List<FriendBlock> findByBlocker(User blocker);

    /**
     * [나를 차단한 사용자 목록 조회]
     * 나를 차단한 사용자들의 FriendBlock 목록을 조회합니다.
     *
     * @param blocked 나 (차단당한 사용자)
     * @return 나를 차단한 사용자 리스트
     */
    List<FriendBlock> findByBlocked(User blocked);

    /**
     * [차단 여부 존재 확인 (Boolean)]
     * Optional 조회 없이 단순 true/false 여부만 필요한 경우 사용합니다.
     *
     * @param blocker 차단한 사용자
     * @param blocked 차단당한 사용자
     * @return 차단 여부 (true = 차단함)
     */
    boolean existsByBlockerAndBlocked(User blocker, User blocked);
}
