package com.hamcam.back.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ApiResponse<T>]
 *
 * 모든 API 응답을 공통 포맷으로 감싸서 반환하기 위한 클래스입니다.
 * 프론트엔드에서 일관된 형식으로 성공/실패 여부 및 데이터를 처리할 수 있도록 설계되었습니다.
 *
 * ✅ 일반적인 응답 형식 예시:
 * {
 *   "success": true,
 *   "message": "요청이 성공적으로 처리되었습니다.",
 *   "data": { ... }
 * }
 *
 * @param <T> 실제 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 요청 성공 여부
     */
    private boolean success;

    /**
     * 응답 메시지 (프론트에게 전달할 메시지)
     */
    private String message;

    /**
     * 실제 데이터
     */
    private T data;

    /**
     * ✅ [성공 응답] - 데이터 포함
     *
     * @param data 응답 데이터
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /**
     * ✅ [성공 응답] - 데이터 없음
     *
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null);
    }

    /**
     * ✅ [실패 응답]
     *
     * @param message 사용자에게 보여줄 에러 메시지
     * @return ApiResponse 객체
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
