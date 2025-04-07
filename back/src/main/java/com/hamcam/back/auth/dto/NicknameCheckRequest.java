package com.hamcam.back.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 닉네임 중복 확인 요청을 처리하기 위한 DTO 클래스
 *
 * [요청 형태]
 * {
 *     "nickname": "example"
 * }
 *
 * [Flow]
 * 1. 클라이언트에서 닉네임을 입력하면 해당 값을 서버로 전달
 * 2. 서버는 DB에서 동일한 닉네임 존재 여부를 확인
 * 3. 결과를 Boolean 값으로 응답 (사용 가능 여부)
 */
@Getter
@Setter
@NoArgsConstructor
public class NicknameCheckRequest {

    /**
     * 사용자로부터 전달받은 닉네임 값
     * null/빈 문자열 여부는 컨트롤러 또는 서비스 계층에서 검증 처리
     */
    private String nickname;
}
