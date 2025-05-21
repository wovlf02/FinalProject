package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 보낸 친구 요청 목록 응답
 */
@Getter
@AllArgsConstructor
public class SentFriendRequestListResponse {
    private List<SentFriendRequestDto> requests;

    /**
     * 보낸 친구 요청 단건 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class SentFriendRequestDto {
        private Long requestId;
        private Long receiverId;
        private String receiverNickname;
        private String receiverProfileImageUrl;
        private String message; // 선택적으로 프론트에서 보낼 수도 있음
        private LocalDateTime requestedAt;
        private String status; // "PENDING", "ACCEPTED", "REJECTED"
    }
}
