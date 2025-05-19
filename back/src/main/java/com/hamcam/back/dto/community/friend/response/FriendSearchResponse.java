package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 닉네임 기반 사용자 검색 응답 DTO
 * <p>
 * 검색 결과로 반환되는 사용자 리스트입니다.
 * 각 사용자에 대해 친구 여부, 요청 여부, 차단 여부를 포함합니다.
 * </p>
 */
@Getter
@AllArgsConstructor
public class FriendSearchResponse {

    /**
     * 검색된 사용자 목록
     */
    private List<UserSearchResult> results;

    /**
     * 사용자 검색 결과 단건 DTO
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserSearchResult {

        /**
         * 사용자 ID
         */
        private Long userId;

        /**
         * 닉네임
         */
        private String nickname;

        /**
         * 프로필 이미지 URL
         */
        private String profileImageUrl;

        /**
         * 이미 친구 관계인지 여부
         */
        private boolean alreadyFriend;

        /**
         * 이미 친구 요청을 보냈는지 여부
         */
        private boolean alreadyRequested;

        /**
         * 현재 사용자가 이 사용자를 차단했는지 여부
         */
        private boolean isBlocked;

        /**
         * 기본값(false)으로 초기화된 사용자 검색 결과 생성
         *
         * @param user 사용자 엔티티
         * @return 기본 상태의 UserSearchResult
         */
        public static UserSearchResult from(User user) {
            return UserSearchResult.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .alreadyFriend(false)
                    .alreadyRequested(false)
                    .isBlocked(false)
                    .build();
        }
    }
}
