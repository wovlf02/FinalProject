package com.hamcam.back.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ErrorResponse]
 *
 * 모든 예외 상황에서 클라이언트에게 반환되는 에러 응답 객체입니다.
 * 에러 코드를 명확히 전달하여 프론트엔드에서 상황별 처리와 UI 메시지 출력을 쉽게 합니다.
 *
 * 예시:
 * {
 *     "status": 401,
 *     "code": "E401",
 *     "message": "인증이 필요합니다."
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP 상태 코드 (예: 400, 401, 403, 404, 500 등)
     */
    private Integer status;

    /**
     * 에러 코드 (예: "E1001", "AUTH_401", "USER_NOT_FOUND")
     */
    private String code;

    /**
     * 사용자에게 보여질 에러 메시지
     */
    private String message;

    /**
     * 상태 없이 코드와 메시지만 포함하는 에러 응답 생성
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 상태, 코드, 메시지를 포함하는 에러 응답 생성
     */
    public static ErrorResponse of(int status, String code, String message) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .build();
    }
}
