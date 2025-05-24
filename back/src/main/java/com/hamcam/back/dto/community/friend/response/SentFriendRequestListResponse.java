package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SentFriendRequestListResponse {

    private List<SentFriendRequestDto> requests;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SentFriendRequestDto {
        private Long requestId;
        private Long receiverId;
        private String receiverNickname;
        private String receiverProfileImageUrl;
        private String message;
        private LocalDateTime requestedAt;
        private String status; // "PENDING", "ACCEPTED", "REJECTED"
    }
}
