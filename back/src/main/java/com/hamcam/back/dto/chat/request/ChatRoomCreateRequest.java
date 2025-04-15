package com.hamcam.back.dto.chat.request;

import lombok.Data;

/**
 * 채팅방 생성 요청 DTO
 * <p>
 * 게시글, 그룹, 스터디 등 다양한 컨텍스트에 따라 채팅방을 생성할 수 있으며,
 * roomType에 따라 연동 대상(referenceId)이 달라진다.
 * </p>
 *
 * 예시: 게시글 기반 채팅 → roomType = "POST", referenceId = 게시글 ID
 */
@Data
public class ChatRoomCreateRequest {

    /**
     * 채팅방 타입 (예: POST, GROUP, STUDY, DIRECT 등)
     */
    private String roomType;

    /**
     * 연동 대상 ID (게시글 ID, 그룹 ID 등)
     */
    private Long referenceId;
}
