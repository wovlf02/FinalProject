package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 친구 관련 액션 처리 응답 DTO
 * <p>
 * 친구 요청, 수락, 삭제, 차단 등의 액션 결과를 반환합니다.
 * 메시지와 함께 boolean 플래그나 대상 ID 등을 함께 제공할 수 있습니다.
 * </p>
 *
 * 예시 응답:
 * <pre>
 * {
 *   "message": "친구 요청이 전송되었습니다.",
 *   "success": true
 * }
 * </pre>
 */
@Data
@AllArgsConstructor
public class FriendActionResponse {

    /**
     * 처리 결과 메시지
     */
    private String message;

    /**
     * 처리 성공 여부
     */
    private boolean success;
}
