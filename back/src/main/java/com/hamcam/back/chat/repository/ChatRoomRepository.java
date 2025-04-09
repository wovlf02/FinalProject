package com.hamcam.back.chat.repository;

import com.hamcam.back.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * [ChatRoomRepository]
 *
 * 채팅방(ChatRoom) 엔티티에 대한 JPA Repository
 * 채팅방 생성, 식별자 기반 조회, 외부 연동(referenceId)로 조회 등의 기능 제공
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 채팅방 ID (roomId)로 채팅방 정보 조회
     * 
     * @param id 채팅방 ID (PK)
     * @return Optional<ChatRoom>
     */
    Optional<ChatRoom> findById(Long id);

    /**
     * 채팅방 타입 (post, study, group 등) 별로 모든 채팅방 조회
     * @param type RoomType (Enum)
     * @return 해당 타입의 모든 채팅방 리스트
     */
    List<ChatRoom> findByRoomType(ChatRoom.RoomType type);

    /**
     * 특정 외부 참조 ID (게시글 ID, 그룹 ID 등)와 채팅방 타입으로 채팅방 조회
     * ex) 게시글 ID가 10인 post 채팅방을 찾을 때 사용
     * 
     * @param referenceId 외부 참조 ID
     * @param roomType 채팅방 종류 (post, study, group 등)
     * @return Optional<ChatRoom> 일치하는 채팅방
     */
    Optional<ChatRoom> findByReferenceIdAndRoomType(Long referenceId, ChatRoom.RoomType roomType);

    /**
     * 외부 참조 ID 기반으로 채팅방이 존재하는지 여부 확인
     * 
     * @param referenceId 외부 ID
     * @param roomType 채팅방 종류
     * @return 존재 여부
     */
    boolean existsByReferenceIdAndRoomType(Long referenceId, ChatRoom.RoomType roomType);
}
