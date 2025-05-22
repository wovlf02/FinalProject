package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [FriendBaseRequest]
 * 단순 userId 전달용 공통 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendBaseRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
