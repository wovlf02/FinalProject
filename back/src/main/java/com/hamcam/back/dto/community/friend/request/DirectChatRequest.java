package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [DirectChatRequest]
 *
 * 사용자가 특정 대상 사용자와 1:1 채팅을 시작할 때 사용하는 요청 DTO입니다.
 * - 기존 채팅방이 존재하면 해당 방을 반환하고, 없으면 새로 생성됩니다.
 *
 * 사용 예시:
 * POST /api/chat/direct/start
 * {
 *   "targetUserId": 5
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class DirectChatRequest {

    /**
     * 1:1 채팅을 시작할 대상 사용자 ID
     */
    @NotNull(message = "대상 사용자 ID는 필수 입력 값입니다.")
    private Long targetUserId;
}
