package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ 추가

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

    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    List<ChatParticipant> findByUser(User user);

    int countByChatRoom(ChatRoom chatRoom);

    @Query("SELECT COUNT(cp.id) FROM ChatParticipant cp WHERE cp.chatRoom.id = :roomId")
    int countByChatRoomId(@Param("roomId") Long roomId); // ✅ @Param 추가

    @Query("""
        SELECT cp FROM ChatParticipant cp
        WHERE cp.chatRoom.id = :roomId
          AND cp.user.id = :userId
    """)
    Optional<ChatParticipant> findByChatRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId); // ✅ @Param 추가
}
