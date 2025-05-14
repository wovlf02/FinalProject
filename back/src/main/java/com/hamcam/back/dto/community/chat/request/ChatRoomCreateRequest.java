package com.hamcam.back.dto.community.chat.request;

import com.hamcam.back.entity.chat.ChatRoomType;
import lombok.Data;

import java.util.List;

/**
 * 채팅방 생성 요청 DTO
 */
@Data
public class ChatRoomCreateRequest {

    /** 채팅방 이름 */
    private String roomName;

    /** 채팅방에 초대할 사용자 ID 목록 (자기 자신 제외) */
    private List<Long> invitedUserIds;

    /** 연동 대상 ID (게시글 ID, 그룹 ID 등. 옵션) */
    private Long referenceId;
}
