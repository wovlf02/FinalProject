package com.hamcam.back.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 조회 및 응답용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String accessToken;        // access 토큰 (필요 시 null 허용)
    private String refreshToken;       // refresh 토큰 (필요 시 null 허용)
    private String username;           // 사용자 ID
    private String name;               // 실명
    private String email;              // 이메일
    private String nickname;           // 닉네임
    private String profileImageUrl;    // 프로필 이미지 URL
}
