package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;

/**
 * ✅ 집중방 채팅 메시지 요청 DTO
 * - 클라이언트가 보낸 메시지 내용만 포함
 */
@Getter
public class FocusChatMessageRequest {
    public String content;
}
