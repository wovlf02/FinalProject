package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String nickname;
    private int grade;
    private String studyHabit;
    private String profileImageUrl;
    private String createdAt;
}
