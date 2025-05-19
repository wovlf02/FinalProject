package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단한 친구 목록 응답 DTO
 * <p>
 * 사용자가 차단한 유저들의 요약 정보를 포함합니다.
 * 각 항목은 사용자 ID, 닉네임, 프로필 이미지 URL로 구성됩니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedFriendListResponse {

    /**
     * 차단된 사용자 목록
     */
    private List<BlockedUserDto> blockedUsers;

    /**
     * 차단된 사용자 요약 DTO
     */
    @Data
    @AllArgsConstructor
    public static class BlockedUserDto {

        /**
         * 사용자 ID
         */
        private Long userId;

        /**
         * 사용자 닉네임
         */
        private String nickname;

        /**
         * 프로필 이미지 URL
         */
        private String profileImageUrl;

        /**
         * User 엔티티로부터 BlockedUserDto 객체 생성
         *
         * @param user 차단된 사용자
         * @return BlockedUserDto 인스턴스
         */
        public static BlockedUserDto from(User user) {
            return new BlockedUserDto(
                    user.getId(),
                    user.getNickname(),
                    user.getProfileImageUrl()
            );
        }
    }
}
