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
     * ✅ 채팅방의 마지막 메시지 내용 (optional)
     */
    private String lastMessage;

    /**
     * ✅ 마지막 메시지 전송 시간 (optional)
     */
    private LocalDateTime lastMessageAt;

    /**
     * ✅ 현재 로그인한 사용자가 읽지 않은 메시지 개수
     */
    private Integer unreadCount;

    /**
     * 참여자 수 반환 (필드 저장 대신 계산 방식)
     */
    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }

    /**
     * ChatRoom 엔티티 → ChatRoomResponse 변환
     * (단순 생성용 — 참여자, 메시지 정보는 이후에 별도로 세팅)
     */
    public static ChatRoomResponse fromEntity(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomType(room.getType().name())
                .createdAt(room.getCreatedAt())
                .representativeImageUrl(room.getRepresentativeImageUrl())
                .participants(List.of()) // 이후 setParticipants()로 주입
                .lastMessage(null)       // 이후 주입
                .lastMessageAt(null)
                .unreadCount(0)
                .build();
    }
}
