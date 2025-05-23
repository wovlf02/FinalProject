package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [DirectChatLookupRequest]
 * 특정 사용자와의 1:1 채팅방 존재 여부 및 상세 조회용 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatLookupRequest {

    /**
     * 현재 로그인한 사용자 ID
     */
    @NotNull(message = "요청자 ID는 필수입니다.")
    private Long userId;

    /**
     * 상대 사용자 ID
     */
    @NotNull(message = "대상 사용자 ID는 필수입니다.")
    private Long targetUserId;
}
