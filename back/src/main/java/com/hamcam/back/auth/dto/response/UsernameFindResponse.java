package com.hamcam.back.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [아이디(username) 찾기 응답 DTO]
 * 
 * 사용자가 입력한 이메일로 조회된 username 정보와 메시지를 함께 반환하는 객체
 * 
 * [사용 API]
 * POST /api/auth/find-username
 * 
 * [응답 예시 -> 성공]
 * {
 *     "success": true,
 *     "username": "example12",
 *     "message": "아이디 찾기에 성공했습니다."
 * }
 * 
 * [응답 예시 -> 실패]
 * {
 *     "success": false,
 *     "username": null,
 *     "message": "해당 이메일로 가입된 사용자를 찾을 수 없습니다."
 * }
 */
@Getter
@AllArgsConstructor
public class UsernameFindResponse {

    /**
     * 요청 처리 성공 여부
     * true: 아이디 조회 성공
     * false: 이메일에 해당하는 사용자가 존재하지 않음
     */
    private boolean success;

    /**
     * 조회된 사용자 아이디 (username)
     * 실패 시에는 null
     */
    private String username;

    /**
     * 사용자에게 전달할 메시지
     * 성공 또는 실패에 따른 안내 문구
     */
    private String message;
}