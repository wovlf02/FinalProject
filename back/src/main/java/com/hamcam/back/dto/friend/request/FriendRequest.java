package com.hamcam.back.dto.friend.request;

import lombok.Data;

/**
 * 친구 요청 전송 DTO
 * <p>
 * 사용자가 특정 사용자에게 친구 요청을 전송할 때 사용됩니다.
 * </p>
 *
 * 예시 요청:
 * {
 *   "receiverId": 5
 * }
 */
@Data
public class FriendRequest {

    /**
     * 친구 요청을 받는 사용자 ID
     */
    private Long receiverId;
}
