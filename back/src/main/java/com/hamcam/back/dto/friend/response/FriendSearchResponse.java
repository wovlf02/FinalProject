package com.hamcam.back.dto.friend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 닉네임 기반 사용자 검색 응답 DTO
 */
@Data
@AllArgsConstructor
public class FriendSearchResponse {

    private List<UserSearchResult> results;

    @Data
    @AllArgsConstructor
    public static class UserSearchResult {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private boolean alreadyFriend;
        private boolean alreadyRequested;
    }
}
