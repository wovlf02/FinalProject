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
 *
 * 채팅방에 대한 전체 정보를 반환합니다.
 * - 채팅방 이름, 참여자 수, 이미지, 생성일 포함
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

    private int participantCount;

    /**
     * 채팅방 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 채팅방 대표 이미지 URL (optional)
     */
    private String representativeImageUrl;

    /**
     * 참여 중인 사용자 정보 목록
     */
    private List<ChatParticipantDto> participants;

    /**
     * 참여자 수 (프론트 최적화용)
     */
    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }
}
