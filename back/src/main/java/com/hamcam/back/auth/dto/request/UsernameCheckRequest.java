package com.hamcam.back.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 아이디(username) 중복 확인 요청 DTO
 *
 * [설명]
 * 사용자가 입력한 아이디가 이미 시스템 내에 존재하는지 확인할 때 사용
 * 회원가입 화면에서 버튼 클릭을 통해 호출됨
 *
 * [요청 예시]
 * {
 *     "username": "example"
 * }
 *
 * [사용 API]
 * POST /api/auth/check-username
 *
 * [Flow]
 * 1. 클라이언트가 사용하고자 하는 아이디를 서버에 전달
 * 2. 서버는 해당 아이디가 DB에 이미 존재하는지 확인
 * 3. 사용 가능 여부를 Boolean 값으로 반환
 */
@Getter
@Setter
@NoArgsConstructor
public class UsernameCheckRequest {

    /**
     * 사용자가 입력한 아이디
     * 로그인 시 사용할 고유 식별값
     * 중복 불가, 소문자/숫자 제한 등의 정책은 프론트 또는 서비스 계층에서 검증
     */
    private String username;
}
