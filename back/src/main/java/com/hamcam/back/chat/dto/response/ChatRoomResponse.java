package com.hamcam.back.chat.dto.response;

import com.hamcam.back.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [ChatRoomResponse]
 * 
 * 채팅방 생성 응답 및 상세 조회 응답 DTO
 * 채팅방 ID, 유형, 연동 기준 ID, 생성일, 응답 메시지 등 포함
 * 
 * [사용 API]
 * POST /api/chat/rooms (생성)
 * GET /api/chat/rooms/{roomId} (상세 조회)
 * 
 * [연관 테이블]
 * CHAT_ROOMS
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {

    /**
     * 채팅방 고유 ID
     * ex) 12
     */
    private Long roomId;

    /**
     * 채팅방 유형
     * POST: 게시글 기반 1:1채팅 (쪽지 등)
     * STUDY: 시시간 문제 풀이용 스터디 채팅
     * GROUP: 전체 그룹 또는 팀 단위 채팅방
     *
     * 클라이언트에서는 이 값을 통해 알림 방식, 뷰 구성 등을 분기 처리할 수 있음
     */
    private ChatRoom.RoomType roomType;

    /**
     * 연동 기준 ID
     * 게시글 ID, 그룹 ID, 스터디 ID 등 외부 요소와의 연결을 위한 ID
     */
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 응답 메시지
     * ex) "채팅방이 성공적으로 생성되었습니다."
     * 또는 상세 조회 시 null 반환 가능
     */
    private String message;
}
