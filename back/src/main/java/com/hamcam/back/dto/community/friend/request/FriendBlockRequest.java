package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendBlockRequest]
 * 사용자를 차단하거나 차단 해제할 때 사용하는 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendBlockRequest {

    @NotNull(message = "targetUserId는 필수입니다.")
    private Long targetUserId;
}
