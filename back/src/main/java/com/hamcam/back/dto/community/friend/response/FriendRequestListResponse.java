package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.friend.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 받은 친구 요청 목록 응답 DTO
 * <p>
 * 로그인한 사용자가 받은 친구 요청 리스트를 반환합니다.
 * 각 요청은 보낸 사람의 정보와 요청 시간을 포함합니다.
 * </p>
 */
@Getter
@AllArgsConstructor
public class FriendRequestListResponse {

    /**
     * 받은 친구 요청 목록
     */
    private List<FriendRequestDto> requests;

    /**
     * 친구 요청 단일 항목 DTO
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class FriendRequestDto {

        /**
         * 친구 요청 고유 ID
         */
        private Long requestId;

        /**
         * 요청 보낸 사용자 ID
         */
        private Long senderId;

        /**
         * 요청 보낸 사용자 닉네임
         */
        private String senderNickname;

        /**
         * 요청 보낸 사용자 프로필 이미지
         */
        private String profileImageUrl;

        /**
         * 친구 요청이 전송된 시각
         */
        private LocalDateTime sentAt;

        /**
         * FriendRequest 엔티티 → FriendRequestDto 변환 메서드
         *
         * @param fr FriendRequest 엔티티
         * @return FriendRequestDto
         */
        public static FriendRequestDto from(FriendRequest fr) {
            return FriendRequestDto.builder()
                    .requestId(fr.getId())
                    .senderId(fr.getSender().getId())
                    .senderNickname(fr.getSender().getNickname())
                    .profileImageUrl(fr.getSender().getProfileImageUrl())
                    .sentAt(fr.getRequestedAt())
                    .build();
        }
    }
}
