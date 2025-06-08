package com.hamcam.back.dto.community.friend.response;

import com.hamcam.back.entity.friend.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class FriendRequestListResponse {

    private List<FriendRequestDto> requests;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FriendRequestDto {
        private Long requestId;
        private Long senderId;
        private String senderNickname;
        private String profileImageUrl;
        private LocalDateTime sentAt;

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
