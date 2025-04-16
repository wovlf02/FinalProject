package com.hamcam.back.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 공통 메시지 응답 DTO
 * <p>
 * 요청 처리 결과를 간단한 메시지와 함께 반환할 때 사용됩니다.
 * 게시글 작성, 삭제, 좋아요 등 모든 도메인에서 공통적으로 사용 가능합니다.
 * </p>
 *
 * 예시:
 * <pre>
 *     {
 *         "message": "게시글이 등록되었습니다.",
 *         "data": 123
 *     }
 * </pre>
 */
@Data
@AllArgsConstructor
public class MessageResponse {

    /**
     * 사용자에게 전달할 메시지 내용
     */
    private String message;

    /**
     * 메시지와 함께 전달되는 추가 데이터 (선택적으로 사용)
     */
    private Object data;

    /**
     * 메시지만 반환할 때 사용하는 생성자
     */
    public MessageResponse(String message) {
        this.message = message;
        this.data = null;
    }
}
