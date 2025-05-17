package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 채팅 메시지(ChatMessage) 관련 JPA Repository
 *
 * - 메시지 저장 및 조회
 * - 채팅방별 페이징 조회
 * - 마지막 메시지
 * - 안 읽은 메시지 수 계산 등
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
     * 채팅방의 전체 메시지 수
     */
    int countByChatRoom(ChatRoom chatRoom);

    /**
     * 채팅방의 메시지를 오래된 순으로 페이징 조회 (예: 무한 스크롤)
     */
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom, Pageable pageable); // ASC

    /**
     * 사용자가 읽지 않은 메시지 수
     * - 내가 보낸 메시지는 제외
     * - 마지막으로 읽은 메시지 ID보다 이후 메시지만 계산
     * - 만약 마지막 읽은 ID가 null이면 전체 메시지 기준
     */
    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.chatRoom = :chatRoom
          AND m.sender <> :user
          AND (:lastReadMessageId IS NULL OR m.id > :lastReadMessageId)
    """)
    int countUnreadMessages(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("user") User user,
            @Param("lastReadMessageId") Long lastReadMessageId
    );
}
