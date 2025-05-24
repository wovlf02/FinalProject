package com.hamcam.back.dto.community.block.response;

import com.hamcam.back.entity.auth.User;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUserListResponse {

    /**
     * 차단한 사용자 목록
     */
    private List<BlockedUserDto> blockedUsers;

    /**
     * 차단된 사용자 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BlockedUserDto {

        /**
         * 차단된 사용자 ID
         */
        private Long userId;

        /**
         * 차단된 사용자 닉네임
         */
        private String nickname;

        /**
         * 차단된 사용자 프로필 이미지 URL
         */
        private String profileImageUrl;

        /**
         * User 엔티티로부터 BlockedUserDto 생성
         */
        public static BlockedUserDto from(User user) {
            return BlockedUserDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }
}
