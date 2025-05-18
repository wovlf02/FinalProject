package com.hamcam.back.dto.community.chat.response;

import com.hamcam.back.entity.chat.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [ChatRoomResponse]
 *
 * 채팅방 상세 정보 응답 DTO입니다.
 * 채팅방의 이름, 타입, 참여자 목록, 생성일, 대표 이미지 등을 포함합니다.
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
     * 채팅방 이름 또는 제목
     */
    private String roomName;

    /**
     * 채팅방 유형 (예: DIRECT, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 채팅방 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 채팅방 대표 이미지 URL (optional)
     */
    private String representativeImageUrl;

    /**
     * 채팅방 참여자 정보 목록
     */
    private List<ChatParticipantDto> participants;

    /**
     * 참여자 수 반환 (필드 저장 대신 계산 방식)
     */
    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }

    /**
     * ChatRoom 엔티티 → ChatRoomResponse 변환
     * (단순 생성용 — 참여자 정보는 이후에 별도로 세팅 필요)
     */
    public static ChatRoomResponse fromEntity(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .createdAt(room.getCreatedAt())
                .representativeImageUrl(room.getRepresentativeImageUrl())
                .participants(List.of()) // 또는 null, 필요 시 setter 사용
                .build();
    }
}
