package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관계(Friend) JPA Repository
 * <p>
 * 사용자 간의 친구 상태를 저장하고 조회합니다.
 * 친구 관계는 user1_id < user2_id 순으로 저장되며,
 * 양방향 친구 여부 확인 시에는 양쪽 조합 모두 검사해야 합니다.
 * </p>
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * 양방향 친구 여부 확인
     */
    Optional<Friend> findByUser1AndUser2(User user1, User user2);

    Optional<Friend> findByUser2AndUser1(User user1, User user2);

    /**
     * 특정 사용자가 포함된 친구 목록 조회
     */
    List<Friend> findByUser1OrUser2(User user1, User user2);

    /**
     * 특정 사용자와 연결된 모든 친구 ID 추출 (단방향 ID 용도)
     */
    List<Friend> findAllByUser1(User user);

    List<Friend> findAllByUser2(User user);
}
