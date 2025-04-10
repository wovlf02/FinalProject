package com.hamcam.back.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [ChatJoinRequest]
 *
 * 채팅방 입장 요청 시 클라이언트에서 서버로 전송되는 DTO 클래스
 * 사용자가 특정 채팅방에 입장할 때 필요한 최소한의 정보(사용자 ID, 닉네임 등)를 포함
 *
 * - 사용 API:
 *   POST /api/chat/rooms/{roomId}/join
 *   DELETE /api/chat/rooms/{roomId}/exit
 *
 * - 연관 DB 테이블: 없음 (서버 측 세션이나 캐시에서 입장 정보 관리 가능)
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatJoinRequest {

    /**
     * 채팅에 참여할 사용자의 고유 ID
     *
     * - USERS 테이블의 user_id 컬럼과 매핑됨
     * - 입장/퇴장 시 사용자의 권한, 입장 이력 관리 등에 활용됨
     */
    private Long userId;

    /**
     * 채팅방에서 표시될 사용자 닉네임
     *
     * - USERS 테이블의 user_nickname 컬럼과 일치
     * - 채팅 메시지 UI에서 발신자 이름으로 사용됨
     */
    private String nickname;
}
