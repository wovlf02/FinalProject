package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendDeleteRequest]
 * 친구 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDeleteRequest {

    @NotNull(message = "friendId는 필수입니다.")
    private Long targetUserId;
}
