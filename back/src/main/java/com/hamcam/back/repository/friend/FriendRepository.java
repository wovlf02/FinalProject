package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ 추가

import java.util.List;
import java.util.Optional;

/**
 * [FriendRepository]
 *
 * 사용자 간 친구 관계(Friend)를 관리하는 JPA Repository입니다.
 * - 친구 추가/삭제/조회
 * - 양방향 친구 여부 확인
 * - 친구 목록 전체 조회 등에 사용됩니다.
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * [양방향 친구 여부 조회 - 조합 1]
     * user → friend 관계 존재 여부 확인
     */
    Optional<Friend> findByUserAndFriend(User user, User friend);

    /**
     * [양방향 친구 여부 조회 - 조합 2]
     * friend → user 관계 존재 여부 확인
     */
    Optional<Friend> findByFriendAndUser(User user, User friend);

    /**
     * [전체 친구 목록 조회]
     * 해당 사용자 기준으로 친구 목록 전체를 조회합니다.
     * (user 또는 friend 컬럼 중 하나라도 일치하면 친구로 간주)
     */
    @Query("SELECT f FROM Friend f WHERE f.user = :user OR f.friend = :user")
    List<Friend> findAllFriendsOfUser(@Param("user") User user); // ✅ @Param 추가

    /**
     * [친구 관계 존재 여부 확인 - 조합 1]
     */
    boolean existsByUserAndFriend(User user, User friend);

    /**
     * [친구 관계 존재 여부 확인 - 조합 2]
     */
    boolean existsByFriendAndUser(User user, User friend);

    /**
     * [양방향 친구 단건 조회]
     * 친구 삭제 등에 활용되며, 두 사용자의 순서와 무관하게 친구 관계를 조회합니다.
     */
    Optional<Friend> findByUserAndFriendOrFriendAndUser(
            User user1, User user2,
            User user3, User user4
    );
}
