package com.hamcam.back.dto.community.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 조회 응답 DTO (요약)
 * <p>
 * 사용자가 참여 중인 모든 채팅방을 목록 형태로 반환할 때 사용됩니다.
 * 최근 메시지 요약 정보와 참여자 수 등 채팅방 미리보기 정보도 포함됩니다.
 * </p>
 */
@Data
@Builder
public class ChatRoomListResponse {

    /**
     * 채팅방 고유 ID
     */
    private Long roomId;

    /**
     * 채팅방 이름 또는 제목
     */
    private String roomName;

    /**
     * 채팅방 유형 (예: POST, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 마지막 메시지 요약 내용
     */
    private String lastMessage;

    /**
     * 마지막 메시지 전송 시간
     */
    private LocalDateTime lastMessageAt;

    /**
     * 현재 참여 중인 사용자 수
     */
    private int participantCount;

    /**
     * 로그인한 사용자의 안 읽은 메시지 수
     */
    private int unreadCount;
}
