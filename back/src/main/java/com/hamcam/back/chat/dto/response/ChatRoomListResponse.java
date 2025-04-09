package com.hamcam.back.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [ChatRoomListResponse]
 *
 * 채팅방 목록 조회 시 클라이언트에 반환되는 응답 DTO
 * 각 채팅방의 고유 ID, 유형, 기준 ID(게시글, 스터디, 그룹 등), 생성일 등 포함
 *
 * [연관 테이블]
 * CHAT_ROOMS
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    /**
     * 채팅방 고유 ID
     * 채팅방 식별용 ID (ex. 101)
     */
    private Long roomId;

    /**
     * 채팅방 타입
     * post: 게시글 기반 1:1 채팅
     * study: 스터디 실시간 토론
     * group: 그룹 내 채팅
     */
    private String roomType;

    /**
     * 외부 연동 기준 ID
     * 게시글 ID, 스터디 ID, 그룹 ID 등
     * 채팅방 생성 시 연관된 객체의 ID
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     * 채팅방이 생성된 시간
     */
    private LocalDateTime createdAt;
}
