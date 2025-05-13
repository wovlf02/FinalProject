package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
}
