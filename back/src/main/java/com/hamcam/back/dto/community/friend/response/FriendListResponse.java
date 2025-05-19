package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 친구 목록 응답 DTO
 * <p>
 * 로그인한 사용자의 친구 목록을 조회할 때 사용됩니다.
 * 각 친구는 ID, 닉네임, 프로필 이미지를 포함합니다.
 * </p>
 */
@Getter
@AllArgsConstructor
public class FriendListResponse {

    /**
     * 친구 목록
     */
    private List<FriendDto> friends;

    /**
     * 친구 단일 항목 DTO
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class FriendDto {
        private Long userId;
        private String nickname;
        private String profileImageUrl;

        /**
         * User 엔티티 → FriendDto 변환 메서드
         *
         * @param user User 엔티티
         * @return FriendDto
         */
        public static FriendDto from(User user) {
            return FriendDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }
}
