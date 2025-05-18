package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
     *
     * @param user 현재 사용자
     * @param friend 친구 대상 사용자
     * @return 친구 관계(Optional)
     */
    Optional<Friend> findByUserAndFriend(User user, User friend);

    /**
     * [양방향 친구 여부 조회 - 조합 2]
     * friend → user 관계 존재 여부 확인
     *
     * @param user 친구 대상 사용자
     * @param friend 현재 사용자
     * @return 친구 관계(Optional)
     */
    Optional<Friend> findByFriendAndUser(User user, User friend);

    /**
     * [전체 친구 목록 조회]
     * 해당 사용자 기준으로 친구 목록 전체를 조회합니다.
     * (user 또는 friend 컬럼 중 하나라도 일치하면 친구로 간주)
     *
     * @param user 사용자
     * @return 친구 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.user = :user OR f.friend = :user")
    List<Friend> findAllFriendsOfUser(User user);

    /**
     * [친구 관계 존재 여부 확인 - 조합 1]
     *
     * @param user 현재 사용자
     * @param friend 상대방
     * @return true = 친구 관계 존재
     */
    boolean existsByUserAndFriend(User user, User friend);

    /**
     * [친구 관계 존재 여부 확인 - 조합 2]
     *
     * @param user 상대방
     * @param friend 현재 사용자
     * @return true = 친구 관계 존재
     */
    boolean existsByFriendAndUser(User user, User friend);

    /**
     * [양방향 친구 단건 조회]
     * 친구 삭제 등에 활용되며, 두 사용자의 순서와 무관하게 친구 관계를 조회합니다.
     *
     * @param user1 사용자 A
     * @param user2 사용자 B
     * @param user3 사용자 B
     * @param user4 사용자 A
     * @return 친구 관계(Optional)
     */
    Optional<Friend> findByUserAndFriendOrFriendAndUser(
            User user1, User user2,
            User user3, User user4
    );
}
