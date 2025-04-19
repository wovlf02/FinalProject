package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관계(Friend) JPA Repository
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * ✅ 양방향 친구 여부 확인 (조합 1)
     */
    Optional<Friend> findByUserAndFriend(User user, User friend);

    /**
     * ✅ 양방향 친구 여부 확인 (조합 2)
     */
    Optional<Friend> findByFriendAndUser(User user, User friend);

    /**
     * ✅ 나와 친구인 모든 관계 조회 (user1 또는 user2가 나)
     */
    @Query("SELECT f FROM Friend f WHERE f.user = :user OR f.friend = :user")
    List<Friend> findAllFriendsOfUser(User user);

    /**
     * ✅ 양방향 친구 여부 존재 확인 (조합 1)
     */
    boolean existsByUserAndFriend(User user, User friend);

    /**
     * ✅ 양방향 친구 여부 존재 확인 (조합 2)
     */
    boolean existsByFriendAndUser(User user, User friend);

    /**
     * ✅ 양방향 친구 관계 조회 (FriendService.deleteFriend 등에서 사용)
     */
    Optional<Friend> findByUserAndFriendOrFriendAndUser(User user1, User user2, User user3, User user4);
}
