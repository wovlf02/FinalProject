package com.hamcam.back.dto.friend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단한 사용자 목록 응답 DTO
 * <p>
 * 내가 차단한 사용자들의 정보를 리스트로 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedFriendListResponse {

    private List<BlockedUserDto> blockedUsers;

    @Data
    @AllArgsConstructor
    public static class BlockedUserDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
