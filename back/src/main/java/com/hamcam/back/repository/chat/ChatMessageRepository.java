package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 채팅 메시지(ChatMessage) 관련 JPA Repository
 * <p>
 * 메시지 전송 저장 및 채팅방 별 메시지 페이징 조회, 마지막 메시지 조회,
 * 안 읽은 메시지 수 카운팅 등에 사용됩니다.
 * </p>
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 채팅방의 메시지를 최신순으로 페이징 조회
     */
    List<ChatMessage> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);

    /**
     * 채팅방의 마지막 메시지 조회 (최신 1개)
     */
    ChatMessage findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    /**
     * 사용자가 읽지 않은 메시지 수 (내가 보낸 메시지는 제외)
     *
     * @param chatRoom           대상 채팅방
     * @param sender             나(자신)
     * @param lastReadMessageId  내가 마지막으로 읽은 메시지 ID
     * @return 읽지 않은 메시지 수
     */
    int countByChatRoomAndSenderNotAndIdGreaterThan(ChatRoom chatRoom, User sender, Long lastReadMessageId);

    List<ChatMessage> findByChatRoom(ChatRoom chatRoom, Pageable pageable); // ASC 정렬 위해

}
