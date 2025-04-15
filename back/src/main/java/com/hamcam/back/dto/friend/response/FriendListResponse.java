package com.hamcam.back.dto.friend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 친구 목록 응답 DTO
 * <p>
 * 현재 로그인한 사용자의 친구 목록을 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class FriendListResponse {

    private List<FriendDto> friends;

    @Data
    @AllArgsConstructor
    public static class FriendDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
