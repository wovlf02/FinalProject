package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendActionResponse {

    private String message;
    private boolean success;

    public static FriendActionResponse success(String message) {
        return FriendActionResponse.builder()
                .message(message)
                .success(true)
                .build();
    }

    public static FriendActionResponse failure(String message) {
        return FriendActionResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}
