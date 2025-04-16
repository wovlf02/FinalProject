package com.hamcam.back.dto.friend.request;

import lombok.Data;

/**
 * 1:1 채팅 시작 요청 DTO
 * <p>
 * 사용자가 특정 대상 사용자와 1:1 채팅을 시작할 때 사용하는 요청입니다.
 * 기존 채팅방이 있으면 해당 방을 반환하고, 없으면 새로 생성합니다.
 * </p>
 *
 * 사용 예시:
 * <pre>
 * POST /api/chat/direct/start
 * {
 *   "targetUserId": 5
 * }
 * </pre>
 */
@Data
public class DirectChatRequest {

    /**
     * 1:1 채팅을 시작할 대상 사용자 ID
     */
    private Long targetUserId;
}
