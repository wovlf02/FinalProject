package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방(ChatRoom) 관련 JPA Repository
 * <p>
 * 채팅방 생성, ID 조회, 특정 연동 리소스 기반 중복 여부 검사 등에 사용됩니다.
 * </p>
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 연동된 referenceId + type 기반으로 채팅방 존재 여부 확인
     */
    Optional<ChatRoom> findByReferenceIdAndType(Long referenceId, String type);

    /**
     * 특정 타입의 채팅방 목록 조회
     */
    List<ChatRoom> findByType(String type);
}
