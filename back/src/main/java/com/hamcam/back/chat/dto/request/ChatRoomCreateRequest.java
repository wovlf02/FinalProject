package com.hamcam.back.chat.dto.request;

import com.hamcam.back.chat.entity.ChatRoom;
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
     * 채팅방 유형 (필수)
     *
     * 채팅방의 목적에 따라 다음 중 하나로 설정
     * -> POST: 게시글 기반 1:1 또는 소규모 토론 채팅
     * -> STUDY: 실시간 문제 풀이/스터디 채팅
     * -> GROUP: 그룹 전체 채팅방
     *
     * 해당 필드는 DB의 ENUM(room_type)과 직접 매핑됨
     * -> 프론트에서는 대문자로 전송하거나 문자열 파싱 로직 필요
     */
    private ChatRoom.RoomType roomType;

    /**
     * 외부 참조 ID (선택)
     *
     * 게시글 ID, 그룹 ID 등 다른 엔티티와의 연결이 필요한 경우 사용
     *
     * ex)
     * 게시글 기반이면 postId 전달
     * 그룹 기반이면 groupId 전달
     *
     * NULL 가능 -> 일반 채팅방의 경우 연동 ID 없음
     */
    private Long referenceId;
}
