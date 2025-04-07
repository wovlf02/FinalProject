package com.hamcam.back.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [로그인 요청 DTO]
 *
 * 클라이언트가 로그인 시 서버로 전송하는 로그인 요청 정보
 * 아이디와 비밀번호를 담고 있으며, 서버에서 이 값을 기반으로 인증 처리 후 JWT 발급
 *
 * [사용 API]
 * POST /api/auth/login
 *
 * [요청 예시]
 * {
 *     "username": "example123",
 *     "password": "example1234!"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    /**
     * 사용자 아이디 (username)
     * 회원가입 시 생성된 고유 아이디
     * DB에서 사용자 조회에 사용
     */
    private String username;

    /**
     * 사용자 비밀번호 (password)
     * 서버에서는 BCrypt로 저장된 해시값과 비교
     * 평문 상태로 입력됨
     */
    private String password;
}
