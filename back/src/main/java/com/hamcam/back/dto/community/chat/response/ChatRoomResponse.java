package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 상세 정보 응답 DTO
 * <p>
 * 채팅방에 대한 전체 정보를 반환합니다. (참여자 목록, 생성 시각, 참조 ID 등 포함)
 * </p>
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    /**
     * 채팅방 고유 ID
     */
    private Long roomId;

    /**
     * 채팅방 제목 또는 이름
     */
    private String roomName;

    /**
     * 채팅방 타입 (예: POST, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 연동된 외부 자원 ID (게시글, 그룹 등)
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 참여 중인 사용자 정보 목록
     */
    private List<ChatParticipantDto> participants;
}
