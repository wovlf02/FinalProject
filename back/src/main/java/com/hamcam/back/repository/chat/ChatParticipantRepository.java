package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 참여자(ChatParticipant) 관련 JPA Repository
 * <p>채팅방 입장/퇴장, 메시지 읽음 관련 로직에 사용됩니다.</p>
 */
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    /**
     * 사용자가 해당 채팅방에 참여 중인지 확인
     *
     * @param chatRoom 대상 채팅방
     * @param user     대상 사용자
     * @return 참여자 엔티티 (Optional)
     */
    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * 채팅방에 참여 중인 사용자 전체 조회
     *
     * @param chatRoom 채팅방
     * @return 참여자 목록
     */
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    /**
     * 사용자가 참여 중인 채팅방 목록 조회
     *
     * @param user 사용자
     * @return 참여 중인 ChatParticipant 목록
     */
    List<ChatParticipant> findByUser(User user);

    /**
     * 채팅방의 참여자 수 조회 (Entity 기반)
     *
     * @param chatRoom 채팅방
     * @return 참여자 수
     */
    int countByChatRoom(ChatRoom chatRoom);

    /**
     * 채팅방의 참여자 수 조회 (ID 기반)
     *
     * @param roomId 채팅방 ID
     * @return 참여자 수
     */
    @Query("SELECT COUNT(cp.id) FROM ChatParticipant cp WHERE cp.chatRoom.id = :roomId")
    int countByChatRoomId(Long roomId);

    /**
     * 채팅방 ID와 사용자 ID로 참여자 조회 (읽음 처리 등에 사용)
     */
    @Query("""
        SELECT cp FROM ChatParticipant cp
        WHERE cp.chatRoom.id = :roomId
          AND cp.user.id = :userId
    """)
    Optional<ChatParticipant> findByChatRoomIdAndUserId(Long roomId, Long userId); // ✅ 추가
}
