package com.hamcam.back.dto.chat.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 채팅방 상세 정보 응답 DTO
 */
@Data
public class ChatRoomResponse {

    /**
     * 채팅방 고유 ID
     */
    private Long roomId;

    /**
     * 채팅방 이름 또는 제목
     */
    private String name;

    /**
     * 채팅방 타입 (POST, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 연동된 외부 ID (게시글 ID, 그룹 ID 등)
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 참여자 목록
     */
    private List<ChatParticipantDto> participants;
}
