package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FriendSearchResponse {

    private List<UserSearchResult> results;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserSearchResult {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private boolean alreadyFriend;
        private boolean alreadyRequested;
        private boolean blocked;

        public static UserSearchResult from(User user) {
            return UserSearchResult.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .alreadyFriend(false)
                    .alreadyRequested(false)
                    .blocked(false)
                    .build();
        }
    }
}
