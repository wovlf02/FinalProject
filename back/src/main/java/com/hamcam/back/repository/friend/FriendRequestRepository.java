package com.hamcam.back.repository.friend;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.friend.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [FriendRequestRepository]
 *
 * 사용자 간 친구 요청(FriendRequest) 정보를 관리하는 JPA Repository입니다.
 * - 중복 요청 방지
 * - 받은 요청 목록 조회
 * - 수락/거절 시 요청 ID 기반 처리 등에 사용됩니다.
 */
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    /**
     * [중복 요청 여부 확인]
     * 특정 사용자가 특정 사용자에게 이미 친구 요청을 보냈는지 확인합니다.
     *
     * @param sender 요청 보낸 사용자
     * @param receiver 요청 받은 사용자
     * @return 요청 정보 (Optional)
     */
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

    /**
     * [받은 친구 요청 목록 조회]
     * 특정 사용자가 받은 모든 친구 요청을 조회합니다.
     *
     * @param receiver 요청 받은 사용자
     * @return 수신된 친구 요청 리스트
     */
    List<FriendRequest> findByReceiver(User receiver);

    /**
     * [요청 ID 기준 단건 조회]
     * 친구 요청 수락/거절 처리 시 사용됩니다.
     *
     * @param id 요청 ID
     * @return 친구 요청(Optional)
     */
    Optional<FriendRequest> findById(Long id);

    /**
     * [중복 요청 여부 확인 (Boolean)]
     * Optional이 아닌 단순 true/false로 친구 요청 여부를 확인합니다.
     *
     * @param sender 요청 보낸 사용자
     * @param receiver 요청 받은 사용자
     * @return 존재 여부
     */
    boolean existsBySenderAndReceiver(User sender, User receiver);
}
