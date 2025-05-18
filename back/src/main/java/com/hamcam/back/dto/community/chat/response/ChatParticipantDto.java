package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ChatParticipantDto]
 *
 * 채팅방 참여자 정보를 나타내는 DTO입니다.
 * 채팅방 내 유저 리스트를 표시할 때 사용되며,
 * 사용자 ID, 닉네임, 프로필 이미지 URL을 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantDto {

    /**
     * 사용자 고유 ID
     */
    private Long userId;

    /**
     * 사용자 닉네임
     */
    private String nickname;

    /**
     * 사용자 프로필 이미지 URL
     */
    private String profileImageUrl;
}
