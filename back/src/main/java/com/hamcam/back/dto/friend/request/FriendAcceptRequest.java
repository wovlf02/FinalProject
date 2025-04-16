package com.hamcam.back.dto.friend.request;

import lombok.Data;

/**
 * 친구 요청 수락 DTO
 * <p>
 * 받은 친구 요청을 수락할 때 요청 ID를 전달합니다.
 * </p>
 *
 * 예시 요청:
 * {
 *   "requestId": 12
 * }
 */
@Data
public class FriendAcceptRequest {

    /**
     * 수락할 친구 요청 ID
     */
    private Long requestId;
}
