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

    /**
     * 요청을 보낸 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 삭제할 친구의 사용자 ID
     */
    @NotNull(message = "friendId는 필수입니다.")
    private Long friendId;
}
