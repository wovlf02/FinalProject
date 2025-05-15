package com.hamcam.back.dto.auth.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String nickname;
    private String name;   // ✅ 추가
    private String email;  // ✅ 추가

    // 모든 필드를 받는 생성자
    public UserDto(String username, String nickname, String name, String email) {
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
    }
}
