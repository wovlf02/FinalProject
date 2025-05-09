package com.hamcam.back.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답을 표준화하기 위한 공통 응답 포맷입니다.
 * 모든 컨트롤러 응답은 ApiResponse<T> 형태로 감싸어 반환됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /**
     * 데이터가 있는 성공 응답
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    /**
     * 데이터 없는 성공 응답
     */
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> ok() {
        return (ApiResponse<T>) new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null);
    }

    /**
     * 실패 응답
     */
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> fail(String message) {
        return (ApiResponse<T>) new ApiResponse<>(false, message, null);
    }
}
