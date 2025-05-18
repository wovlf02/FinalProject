package com.hamcam.back.dto.community.friend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 친구 관련 액션 처리 응답 DTO
 * <p>
 * 친구 요청, 수락, 거절, 삭제, 차단 등과 같은 친구 관련 액션의 결과를 나타냅니다.
 * 메시지와 처리 성공 여부를 함께 전달하여 클라이언트에서의 UI 처리 및 안내 메시지 표시를 돕습니다.
 * </p>
 *
 * 예시 응답:
 * {
 *   "message": "친구 요청이 전송되었습니다.",
 *   "success": true
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendActionResponse {

    /**
     * 처리 결과 메시지
     * 예: "친구 요청이 전송되었습니다.", "차단을 해제했습니다." 등
     */
    private String message;

    /**
     * 요청 처리 성공 여부 (true/false)
     */
    private boolean success;

    /**
     * 정적 팩토리 메서드 - 성공 응답
     */
    public static FriendActionResponse success(String message) {
        return FriendActionResponse.builder()
                .message(message)
                .success(true)
                .build();
    }

    /**
     * 정적 팩토리 메서드 - 실패 응답
     */
    public static FriendActionResponse failure(String message) {
        return FriendActionResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}
