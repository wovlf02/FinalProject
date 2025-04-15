package com.hamcam.back.dto.friend.request;

import lombok.Data;

/**
 * 친구 요청 거절 DTO
 * <p>
 * 받은 친구 요청을 거절할 때 사용됩니다.
 * </p>
 *
 * 예시 요청:
 * {
 *   "requestId": 12
 * }
 */
@Data
public class FriendRejectRequest {

    /**
     * 거절할 친구 요청 ID
     */
    private Long requestId;
}
