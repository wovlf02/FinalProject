package com.hamcam.back.dto.study.team.socket.response;

import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 집중방 채팅 메시지 응답 DTO
 * - Redis 저장 및 프론트 브로드캐스트용
 */
@Getter
@Builder
public class FocusChatMessageResponse {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private long timestamp;
}
