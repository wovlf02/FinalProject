package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendRejectRequest]
 * 친구 요청 거절 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRejectRequest {

    @NotNull(message = "requestId는 필수입니다.")
    private Long requestId;
}
