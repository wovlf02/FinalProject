package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [DirectChatRequest]
 *
 * 1:1 채팅방 생성 요청 DTO
 * 친구 또는 일반 사용자와의 개인 채팅을 시작할 때 사용됩니다.
 * 이미 존재하는 경우 해당 채팅방을 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatRequest {

    /**
     * 채팅을 시작할 대상 사용자 ID
     */
    @NotNull(message = "대상 사용자 ID는 필수입니다.")
    private Long targetUserId;
}
