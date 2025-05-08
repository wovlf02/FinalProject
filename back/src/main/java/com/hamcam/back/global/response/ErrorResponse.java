package com.hamcam.back.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 예외 상황에서 클라이언트에게 반환되는 에러 응답 객체
 * 에러 코드를 명확히 저달하여 프론트엔드에서 상황별 처리 가능
 *
 * 예:
 * {
 *     "code": "C001",
 *     "message": "잘못된 요청입니다."
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * 에러 코드
     * ex) "C001", "C404"
     * ErrorCode enum에서 정의됨
     */
    private String code;

    /**
     * 사용자에게 보여질 에러 메시지
     * ex) "잘못된 요청입니다.", "권한이 없습니다." 등
     */
    private String message;
}
