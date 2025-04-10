package com.hamcam.back.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [ChatRoomCreationRequest]
 *
 * 새로운 채팅방을 생성할 때 클라이언트가 서버로 전송하는 요청 DTO
 * 게시글 기반, 스터디, 그룹등 다양한 목적의 채팅방을 생성할 수 있으며,
 * 외부 참조 ID (게시글 ID, 그룹 ID 등)를 함께 저장 가능
 *
 * [사용 API]
 * POST /api/chat/rooms
 *
 * [연관 테이블]
 * CHAT_ROOMS 테이블
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomCreateRequest {

    /**
     * 채팅방 타입
     *
     * 필수 필드
     * 채팅방의 목적에 따라 다음 중 하나로 설정
     * -> post: 게시글 기반 1:1 또는 소규모 토론 채팅
     * -> study: 실시간 문제 풀이/스터디 채팅
     * -> group: 그룹 전체 채팅방
     * DB의 ENUM(room_type)과 연동됨
     */
    private String roomType;

    /**
     * 외부 참조 ID
     * 
     * 선택필드
     * 게시글 ID, 그룹 ID 등 외부 엔티티를 참조할 경우 설정
     * ex) 게시글 기반 채팅방일 경우 postId
     * NULL 가능 (ex. 익명 채팅방 등)
     */
    private Long referenceId;
}
