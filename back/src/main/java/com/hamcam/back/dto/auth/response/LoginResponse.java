package com.hamcam.back.dto.auth.response;

import com.hamcam.back.entity.auth.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO (LocalStorage 저장용)
 */
@Getter
@Builder
public class LoginResponse {

    private Long userId;
    private String username;
    private String email;
    private String name;
    private String nickname;
    private Integer grade;
    private String studyHabit;
    private String phone;
    private String profileImageUrl;

    public static LoginResponse from(User user) {
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .grade(user.getGrade())
                .studyHabit(user.getStudyHabit())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
