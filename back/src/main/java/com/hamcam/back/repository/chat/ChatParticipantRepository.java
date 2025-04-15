package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatParticipant;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 참여자(ChatParticipant) 관련 JPA Repository
 * <p>
 * 채팅방 입장/퇴장 시 참여자 관리에 사용됩니다.
 * </p>
 */
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    /**
     * 채팅방 ID와 사용자 ID로 참여 여부 확인
     */
    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * 특정 채팅방에 참여 중인 사용자 목록 조회
     */
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    /**
     * 사용자가 참여 중인 모든 채팅방 조회
     */
    List<ChatParticipant> findByUser(User user);
}
