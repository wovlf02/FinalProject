package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendAcceptRequest]
 * 친구 요청 수락 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendAcceptRequest {

    @NotNull(message = "requestId는 필수입니다.")
    private Long requestId;
}
