package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendCancelRequest]
 * 내가 보낸 친구 요청을 취소할 때 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendCancelRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
