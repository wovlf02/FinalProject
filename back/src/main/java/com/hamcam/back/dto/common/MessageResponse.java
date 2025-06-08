package com.hamcam.back.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 메시지 응답 DTO
 *
 * 요청 처리 결과를 간단한 메시지와 함께 반환할 때 사용됩니다.
 * 게시글 작성, 삭제, 좋아요 등 여러 도메인에서 공통적으로 활용 가능합니다.
 *
 * 예시:
 * {
 *   "message": "게시글이 등록되었습니다.",
 *   "data": 123
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    /**
     * 사용자에게 전달할 메시지
     */
    private String message;

    /**
     * 메시지와 함께 전달할 부가 데이터 (nullable)
     */
    private Object data;

    /**
     * 메시지만 포함된 응답 생성
     *
     * @param message 전달할 메시지
     * @return MessageResponse
     */
    public static MessageResponse of(String message) {
        return new MessageResponse(message, null);
    }

    /**
     * 메시지 + 데이터 포함 응답 생성
     *
     * @param message 메시지
     * @param data    추가 데이터
     * @return MessageResponse
     */
    public static MessageResponse of(String message, Object data) {
        return new MessageResponse(message, data);
    }
}
