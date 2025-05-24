package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [DirectChatRequest]
 * 1:1 채팅 시작 요청 DTO
 * 요청자(userId)와 상대방(targetUserId)를 함께 전달
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatRequest {

    /**
     * 채팅 상대 사용자 ID
     */
    @NotNull(message = "대상 사용자 ID는 필수입니다.")
    private Long targetUserId;
}
