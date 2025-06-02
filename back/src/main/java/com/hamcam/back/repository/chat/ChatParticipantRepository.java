package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * [ChatParticipantRepository]
 *
 * 채팅방 참여자 관련 JPA Repository입니다.
 * - 참여자 입장 여부 확인
 * - 참여자 목록 및 수 조회
 * - 메시지 읽음 처리 등에서 사용됩니다.
 */
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    /**
     * [사용자의 채팅방 참여 여부 확인]
     * 주어진 채팅방과 사용자에 대해 참여 이력을 조회합니다.
     *
     * @param chatRoom 채팅방 엔티티
     * @param user 사용자 엔티티
     * @return 참여 정보 (Optional)
     */
    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * [채팅방 참여자 전체 조회]
     * 특정 채팅방에 참여 중인 사용자 전체 목록을 반환합니다.
     *
     * @param chatRoom 대상 채팅방
     * @return 해당 채팅방의 참여자 목록
     */
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    /**
     * [사용자의 참여 채팅방 목록 조회]
     * 특정 사용자가 참여 중인 모든 채팅방(참여 정보) 목록을 반환합니다.
     *
     * @param user 사용자
     * @return 참여자 엔티티 목록
     */
    List<ChatParticipant> findByUser(User user);

    /**
     * [채팅방의 참여자 수 조회 - 엔티티 기반]
     *
     * @param chatRoom 채팅방
     * @return 참여 중인 사용자 수
     */
    int countByChatRoom(ChatRoom chatRoom);

    /**
     * [채팅방의 참여자 수 조회 - ID 기반]
     * 채팅방 ID만으로 참여자 수를 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @return 참여자 수
     */
    @Query("SELECT COUNT(cp.id) FROM ChatParticipant cp WHERE cp.chatRoom.id = :roomId")
    int countByChatRoomId(Long roomId);

    /**
     * [채팅방 ID + 사용자 ID 기준 참여자 조회]
     * 읽음 처리, 알림 등에서 활용되는 쿼리입니다.
     *
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 참여자 정보 (Optional)
     */
    @Query("""
        SELECT cp FROM ChatParticipant cp
        WHERE cp.chatRoom.id = :roomId
          AND cp.user.id = :userId
    """)
    Optional<ChatParticipant> findByChatRoomIdAndUserId(Long roomId, Long userId);
}
