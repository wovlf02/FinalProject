package com.hamcam.back.dto.friend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.hamcam.back.entity.friend.FriendRequest;

/**
 * 받은 친구 요청 목록 응답 DTO
 * <p>
 * 아직 수락 또는 거절하지 않은 친구 요청들을 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class FriendRequestListResponse {

    private List<FriendRequestDto> requests;

    @Data
    @AllArgsConstructor
    public static class FriendRequestDto {
        private Long requestId;
        private Long senderId;
        private String senderNickname;
        private String profileImageUrl;
        private LocalDateTime sentAt;

        public static FriendRequestDto from(FriendRequest fr) {
            return new FriendRequestDto(
                    fr.getId(),
                    fr.getSender().getId(),
                    fr.getSender().getNickname(),
                    fr.getSender().getProfileImageUrl(),
                    fr.getRequestedAt()
            );
        }
    }

}
