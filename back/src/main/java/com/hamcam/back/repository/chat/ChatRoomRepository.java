package com.hamcam.back.repository.chat;

import com.hamcam.back.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [ChatRoomRepository]
 *
 * 채팅방(ChatRoom) 관련 JPA Repository입니다.
 * - 채팅방 생성 여부 확인
 * - 타입별 채팅방 조회
 * - 외부 리소스 연동(referenceId)을 통한 중복 방지 등에서 사용됩니다.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * [연동 리소스 기반 채팅방 조회]
     * 예: 특정 문제 풀이, 캠스터디 등 연동된 리소스 ID 및 타입을 기반으로
     * 채팅방 중복 생성을 방지하거나 기존 방을 재사용합니다.
     *
     * @param referenceId 연동된 외부 리소스 ID (ex. 문제 ID, 과목 ID 등)
     * @param type 채팅방 타입 (ex. DIRECT, GROUP, STUDY 등)
     * @return 해당 조건에 맞는 채팅방 (없을 경우 Optional.empty)
     */
    Optional<ChatRoom> findByReferenceIdAndType(Long referenceId, String type);

    /**
     * [타입 기반 채팅방 목록 조회]
     * 주어진 타입의 채팅방들을 모두 조회합니다.
     *
     * @param type 채팅방 타입 (예: DIRECT, GROUP, STUDY)
     * @return 해당 타입의 채팅방 리스트
     */
    List<ChatRoom> findByType(String type);
}
