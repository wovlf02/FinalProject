package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 채팅 메시지(ChatMessage) 관련 JPA Repository
 * <p>
 * 메시지 전송 저장 및 채팅방 별 메시지 페이징 조회 등에 사용됩니다.
 * </p>
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 채팅방의 메시지를 최신순으로 페이징 조회
     */
    List<ChatMessage> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);
}
