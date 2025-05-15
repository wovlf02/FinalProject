package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

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

        public static BlockedUserDto from(User user) {
            return new BlockedUserDto(
                    user.getId(),
                    user.getNickname(),
                    user.getProfileImageUrl()
            );
        }
    }
}
