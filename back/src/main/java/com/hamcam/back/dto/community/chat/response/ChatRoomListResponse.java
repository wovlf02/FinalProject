package com.hamcam.back.dto.community.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 조회 응답 DTO (요약)
 *
 * 사용자가 참여 중인 채팅방 리스트를 불러올 때 사용됩니다.
 * 마지막 메시지 정보, 안 읽은 메시지 수, 참여자 수, 대표 이미지 등을 포함합니다.
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
     * 채팅방 유형 (예: DIRECT, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 마지막 메시지 내용 (텍스트일 경우만, 파일이면 파일명 또는 유형 표시)
     */
    private String lastMessage;

    /**
     * 마지막 메시지 전송 시간
     */
    private LocalDateTime lastMessageAt;

    /**
     * 마지막 메시지 작성자의 닉네임
     */
    private String lastSenderNickname;

    /**
     * 마지막 메시지 작성자의 프로필 이미지 URL
     */
    private String lastSenderProfileImageUrl;

    /**
     * 마지막 메시지 유형 (예: TEXT, IMAGE, FILE, ENTER 등)
     */
    private String lastMessageType;

    /**
     * 현재 채팅방 참여자 수
     */
    private int participantCount;

    /**
     * 로그인한 사용자의 안 읽은 메시지 수
     */
    private int unreadCount;

    /**
     * 총 메시지 수 (이 채팅방에서)
     */
    private int totalMessageCount;

    /**
     * 대표 이미지 URL (있을 경우만 프론트에서 보여줌, 그룹방 대표 이미지 등)
     */
    private String profileImageUrl;
}
