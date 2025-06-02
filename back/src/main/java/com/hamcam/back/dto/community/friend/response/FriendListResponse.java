package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class FriendListResponse {
    private List<FriendDto> onlineFriends;
    private List<FriendDto> offlineFriends;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FriendDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
