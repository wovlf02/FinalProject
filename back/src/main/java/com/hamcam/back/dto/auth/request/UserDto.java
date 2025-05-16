package com.hamcam.back.dto.auth.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String nickname;
    private String name;
    private String email;
    private String profileImageUrl;
    private Integer grade;         // 학년
    private String studyHabit;     // 공부 습관
    private String createdAt;      // 생성일

    public UserDto(String username, String nickname, String name, String email, String profileImageUrl,Integer grade, String studyHabit, String createdAt) {
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.grade = grade;
        this.studyHabit = studyHabit;
        this.createdAt = createdAt;
    }
}
