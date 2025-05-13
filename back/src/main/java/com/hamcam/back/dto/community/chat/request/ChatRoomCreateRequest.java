package com.hamcam.back.dto.community.chat.request;

import com.hamcam.back.entity.chat.ChatRoomType;
import lombok.Data;

/**
 * 채팅방 생성 요청 DTO
 */
@Data
public class ChatRoomCreateRequest {

    /** 채팅방 이름 */
    private String roomName;

    /** 채팅방 타입 (예: POST, GROUP, STUDY, DIRECT 등) */
    private ChatRoomType roomType;

    /** 연동 대상 ID (게시글 ID, 그룹 ID 등) */
    private Long referenceId;
}
