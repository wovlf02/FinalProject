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
 * [ChatMessageRepository]
 *
 * 채팅 메시지 관련 데이터베이스 작업을 처리하는 JPA Repository입니다.
 * - 메시지 저장, 조회, 페이징 처리
 * - 안 읽은 메시지 수 계산 등
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * [채팅방 메시지 최신순 조회]
     * 채팅방에 속한 메시지를 최신순으로 페이징 조회합니다.
     *
     * @param chatRoom 대상 채팅방
     * @param pageable 페이징 정보 (ex. PageRequest.of(0, 30))
     * @return 최신 메시지부터 정렬된 메시지 목록
     */
    List<ChatMessage> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);

    /**
     * [채팅방 마지막 메시지 조회]
     * 해당 채팅방에서 가장 최근에 보낸 메시지를 조회합니다.
     *
     * @param chatRoom 대상 채팅방
     * @return 가장 마지막 메시지 (없으면 null)
     */
    ChatMessage findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    /**
     * [채팅방 메시지 총 개수]
     * 해당 채팅방에 존재하는 전체 메시지 수를 반환합니다.
     *
     * @param chatRoom 대상 채팅방
     * @return 메시지 수
     */
    int countByChatRoom(ChatRoom chatRoom);

    /**
     * [채팅방 메시지 오래된 순 페이징 조회]
     * 채팅방 메시지를 오래된 순서로 페이징하여 조회합니다.
     * (무한 스크롤 등에서 사용)
     *
     * @param chatRoom 대상 채팅방
     * @param pageable 페이징 정보
     * @return 오래된 메시지부터 정렬된 메시지 목록
     */
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom, Pageable pageable); // default: ASC

    /**
     * [안 읽은 메시지 수 조회]
     *
     * 조건:
     * - 채팅방 내 메시지 중, 내가 보낸 메시지는 제외
     * - 마지막으로 읽은 메시지 ID 이후에 작성된 메시지만 집계
     * - 만약 마지막 읽은 메시지 ID가 null이면 모든 메시지 대상
     *
     * @param chatRoom 대상 채팅방
     * @param user 읽지 않은 메시지를 확인할 사용자
     * @param lastReadMessageId 마지막으로 읽은 메시지 ID (nullable)
     * @return 읽지 않은 메시지 수
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

    @Query("""
    SELECT COUNT(m) FROM ChatMessage m
    WHERE m.chatRoom = :chatRoom
      AND m.sender.id <> :userId
      AND m.id NOT IN (
          SELECT cr.message.id FROM com.hamcam.back.entity.chat.ChatRead cr
          WHERE cr.user.id = :userId
      )
""")
    int countUnreadMessagesByReadTable(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("userId") Long userId
    );

}
