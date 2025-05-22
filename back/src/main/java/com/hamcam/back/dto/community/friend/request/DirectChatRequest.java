package com.hamcam.back.dto.community.friend.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [DirectChatRequest]
 * 특정 사용자와의 1:1 채팅방을 시작하거나 조회할 때 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatRequest {

    /**
     * 요청자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 채팅할 대상 사용자 ID
     */
    @NotNull(message = "targetUserId는 필수입니다.")
    private Long targetUserId;
}
