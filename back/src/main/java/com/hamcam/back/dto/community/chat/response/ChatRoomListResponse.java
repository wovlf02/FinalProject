package com.hamcam.back.dto.community.chat.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [ChatRoomListResponse]
 *
 * 사용자가 참여 중인 채팅방 목록을 응답할 때 사용되는 DTO입니다.
 * 각 채팅방의 마지막 메시지, 안 읽은 메시지 수, 참여자 수, 프로필 이미지 등을 포함합니다.
 */
@Getter
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
     * 채팅방 유형 (DIRECT, GROUP, STUDY 등)
     */
    private String roomType;

    /**
     * 마지막 메시지 내용 (텍스트인 경우 본문, 이미지/파일인 경우 유형)
     */
    private String lastMessage;

    /**
     * 마지막 메시지 전송 시각
     */
    private LocalDateTime lastMessageAt;

    /**
     * 마지막 메시지를 보낸 사용자 닉네임
     */
    private String lastSenderNickname;

    /**
     * 마지막 메시지를 보낸 사용자의 프로필 이미지 URL
     */
    private String lastSenderProfileImageUrl;

    /**
     * 마지막 메시지 유형 (TEXT, IMAGE, FILE, ENTER 등)
     */
    private String lastMessageType;

    /**
     * 현재 채팅방에 참여 중인 사용자 수
     */
    private int participantCount;

    /**
     * 현재 로그인 사용자의 안 읽은 메시지 수
     */
    private int unreadCount;

    /**
     * 이 채팅방에 존재하는 총 메시지 수
     */
    private int totalMessageCount;

    /**
     * 채팅방 대표 이미지 URL (GROUP 채팅일 경우 등)
     */
    private String profileImageUrl;
}
