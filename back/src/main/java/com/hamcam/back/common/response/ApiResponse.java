package com.hamcam.back.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * [ApiResponse]
 *
 * 모든 성공 응답에 사용되는 공통 응답 형식 DTO입니다.
 * API 호출이 성공한 경우, 클라이언트에 메시지와 함께 필요한 데이터를 전달합니다.
 *
 * [제공 필드]
 * - message: 성공 메시지 (예: "요청이 성공적으로 처리되었습니다.")
 * - data: 응답 객체 (제네릭 타입으로 유연하게 사용 가능)
 * - statusCode: HTTP 상태 코드 (예: 200, 201 등)
 *
 * [사용 예시]
 * return ResponseEntity.ok(ApiResponse.of("게시글이 등록되었습니다.", createdPostDto));
 * return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("회원가입 완료", null));
 */
@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * 클라이언트에 전달할 메시지
     * 예: "요청이 성공적으로 처리되었습니다."
     */
    private final String message;

    /**
     * 실제 응답 데이터 (null 허용)
     */
    private final T data;

    /**
     * HTTP 상태 코드 (예: 200, 201)
     */
    private final int statusCode;

    /**
     * 공통 응답 생성 메서드
     *
     * @param message 응답 메시지
     * @param data    응답 데이터
     * @param <T>     데이터 타입
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> of(String message, T data, int statusCode) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .statusCode(statusCode)
                .build();
    }
}
