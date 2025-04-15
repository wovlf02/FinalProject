package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 성공 시 반환되는 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Access Token
     * 유효기간: 1시간
     * API 요청 새 Authorization 헤더에 포함하여 사용
     */
    private String accessToken;

    /**
     * JWT Refresh Token
     * 유효기간: 30일
     * 만료 시 새로운 Access Token을 발급받기 위해 사용
     */
    private String refreshToken;

    /**
     * 아이디
     * 로그인한 사용자 정보 제공
     */
    private String username;

    /**
     * 이메일
     * 로그인한 사용자 정보 제공
     */
    private String email;

    /**
     * 이름
     * 로그인한 사용자 정보 제공
     */
    private String name;
}
