package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FriendListResponse {

    private List<FriendDto> friends;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FriendDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;

        public static FriendDto from(User user) {
            return FriendDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }
}
