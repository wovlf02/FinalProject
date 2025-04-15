package com.hamcam.back.dto.chat.request;

import lombok.Data;

/**
 * 채팅방 입장 또는 퇴장 요청 DTO
 * <p>
 * 채팅방 참여자의 입장/퇴장을 서버에 전달할 때 사용됨.
 * 입장자 수 조정 또는 자동 삭제 로직에 활용됨.
 * </p>
 */
@Data
public class ChatJoinRequest {

    /**
     * 입장 또는 퇴장하는 사용자 ID
     */
    private Long userId;
}
