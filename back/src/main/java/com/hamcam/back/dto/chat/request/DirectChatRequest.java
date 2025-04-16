package com.hamcam.back.dto.chat.request;

import lombok.Data;

/**
 * 1:1 채팅방 생성 요청 DTO
 * <p>
 * 친구 또는 일반 사용자와의 개인 채팅을 시작할 때 사용됨.
 * 이미 존재하는 경우 해당 채팅방을 반환함.
 * </p>
 */
@Data
public class DirectChatRequest {

    /**
     * 채팅을 시작할 대상 사용자 ID
     */
    private Long targetUserId;
}
