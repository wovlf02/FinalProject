package com.hamcam.back.dto.community.friend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 친구 요청 전송 요청 DTO
 * - 친구 요청을 받을 대상 사용자의 ID를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestSendRequest {

    /** 요청 대상 사용자 ID */
    private Long targetUserId;
}
