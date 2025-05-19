package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [FriendRequestSendRequest]
 *
 * 친구 요청 전송 요청 DTO입니다.
 * 요청 대상 사용자의 ID를 포함하며,
 * 해당 ID는 서버에서 유효 사용자 검증에 사용됩니다.
 *
 * 예시 요청:
 * {
 *   "targetUserId": 42
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestSendRequest {

    /**
     * 친구 요청을 받을 대상 사용자 ID
     */
    @NotNull(message = "친구 요청 대상 ID는 필수 입력 값입니다.")
    private Long targetUserId;
}
