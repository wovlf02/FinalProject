package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [비밀번호 재설정 응답 DTO]
 *
 * 서버가 비밀번호 변경 요청 처리 결과를 클라이언트에 응답할 때 사용
 * 성공 여부와 사용자에게 전달할 메시지 포함
 *
 * [사용 API]
 * POST /api/auth/reset-password
 *
 * [응답 예시 -> 성공]
 * {
 *     "success": true,
 *     "message": "비밀번호가 성공적으로 변경되었습니다."
 * }
 *
 * [응답 예시 -> 실패]
 * {
 *     "success": false,
 *     "message": "사용자를 찾을 수 없습니다."
 * }
 */
@Getter
@AllArgsConstructor
public class ResetPasswordResponse {

    /**
     * 처리 성공 여부
     * true: 비밀번호 변경 성공
     * false: 실패 (사용자 없음, 예외 발생 등)
     */
    private boolean success;

    /**
     * 처리 결과 메시지
     * 사용자에게 안내할 메시지 (성공/실패 사유 등)
     */
    private String message;
}
