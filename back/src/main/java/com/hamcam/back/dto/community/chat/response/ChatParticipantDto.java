package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 채팅방 참여자 정보 DTO
 */
@Data
@AllArgsConstructor
public class ChatParticipantDto {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자 닉네임
     */
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    private String profileImageUrl;
}
