package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockedFriendListResponse {

    private List<BlockedUserDto> blockedUsers;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BlockedUserDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;

        public static BlockedUserDto from(User user) {
            return BlockedUserDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }
}
