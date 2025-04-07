package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 아이디(username) 중복 확인 응답 DTO
 * 
 * [응답 예시]
 * {
 *     "available": true
 * }
 * 
 * [필드 설정]
 * available: true일 경우 사용 가능한 아이디, false일 경우 이미 존재하는 아이디
 * 
 * [사용 API]
 * POST /api/auth/check-username
 */
@Getter
@AllArgsConstructor
public class UsernameCheckResponse {

    /**
     * 아이디 사용 가능 여부
     * true: 사용 가능 (중복 아님)
     * false: 중복된 아이디로 사용 불가
     */
    private boolean available;
}
