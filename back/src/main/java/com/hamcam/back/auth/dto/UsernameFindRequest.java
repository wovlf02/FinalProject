package com.hamcam.back.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [아이디(username) 찾기 요청 DTO]
 *
 * 사용자가 아이디를 잊어버린 경우, 이메일을 기반으로 아이디를 찾고자 할 때 요청되는 객체
 * 이메일로 가입된 사용자의 아이디를 서버에서 조회
 *
 * [사용 API]
 * POST /api/auth/find-username
 *
 * [요청 예시]
 * {
 *     "email": "user@example.com"
 * }
 *
 * [Flow]
 * 1. 클라이언트에서 이메일 주소 입력
 * 2. 서버는 해당 이메일로 가입된 사용자를 조회
 * 3. 사용자가 존재할 경우 username 반환
 */
@Getter
@Setter
@NoArgsConstructor
public class UsernameFindRequest {

    /**
     * 사용자 이메일 주소
     * 이 이메일로 가입된 사용자의 username을 조회
     */
    private String email;
}
