package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 친구 목록 응답 DTO
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

        /**
         * User 엔티티로부터 FriendDto 변환
         */
        public static FriendDto from(User user) {
            return new FriendDto(
                    user.getId(),
                    user.getNickname(),
                    user.getProfileImageUrl()
            );
        }
    }
}
