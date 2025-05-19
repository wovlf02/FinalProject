package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [FriendRejectRequest]
 *
 * 친구 요청 거절 요청 DTO입니다.
 * 수신한 친구 요청의 고유 ID(requestId)를 전달하여 요청을 거절합니다.
 *
 * 예시 요청:
 * {
 *   "requestId": 12
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class FriendRejectRequest {

    /**
     * 거절할 친구 요청 ID
     */
    @NotNull(message = "요청 ID는 필수 입력 값입니다.")
    private Long requestId;
}
