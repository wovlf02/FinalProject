package com.hamcam.back.dto.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 채팅방 상세 정보 응답 DTO
 * <p>
 * 채팅방 상세 정보를 반환할 때 사용되며,
 * 참여자 목록, 생성 시간, 채팅방의 타입 및 외부 연동 정보 등을 포함합니다.
 * </p>
 */
@Data
@Builder
public class ChatRoomResponse {

    /**
     * 채팅방 고유 ID
     */
    private Long roomId;

    /**
     * 채팅방 이름 또는 제목
     */
    private String roomName;

    /**
     * 채팅방 타입 (예: POST, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 연동된 외부 ID (게시글 ID, 그룹 ID 등)
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 채팅방에 참여 중인 사용자 목록
     */
    private List<ChatParticipantDto> participants;
}
