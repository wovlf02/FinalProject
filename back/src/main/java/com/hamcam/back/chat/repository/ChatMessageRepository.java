package com.hamcam.back.chat.repository;

import com.hamcam.back.chat.entity.ChatMessage;
import com.hamcam.back.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [ChatMessageRepository]
 *
 * 실시간 채팅 메시지 데이터를 데이터베이스에서 조회/저장/삭제하는 JPA 리포지토리 인터페이스
 * 기본적인 CRUD 외에도 채팅방별 메시지 목록 조회, 시간 기준 필터링 등 다양한 쿼리 지원
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 채팅방에 속한 모든 메시지를 시간 순으로 정렬하여 조회
     * 
     * @param chatRoom 조회 대상 채팅방 엔티티
     * @return 해당 채팅방에 속한 메시지 리스트 (오래된 순)
     */
    List<ChatMessage> findByChatRoomOrderBySendAtAsc(ChatRoom chatRoom);

    /**
     * 특정 채팅방에서 특정 시각 이후에 전송된 메시지 조회
     * WebSocket 재연결 시 최신 메시지만 가져오기 등에 사용 가능
     * 
     * @param chatRoom 채팅방
     * @param since 기준 시각 (LocalDateTime)
     * @return 최근 메시지 목록
     */
    List<ChatMessage> findByChatRoomAndSendAtAfterOrderBySendAtAsc(ChatRoom chatRoom, LocalDateTime since);

    /**
     * 가장 최근에 전송된 메시지 1개 조회
     * 채팅방 목록에서 마지막 메시지 프리뷰를 보여줄 때 사용
     * 
     * @param chatRoom 채팅방
     * @return 최신 메시지 (null일 수도 있음)
     */
    ChatMessage findTopByChatRoomOrderBySendAtDesc(ChatRoom chatRoom);
}
