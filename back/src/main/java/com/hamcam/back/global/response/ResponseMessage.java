package com.hamcam.back.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ResponseMessage]
 *
 * 클라이언트에게 단순 메시지를 반환하는 데 사용되는 응답 포맷입니다.
 * 주로 등록, 수정, 삭제 등의 요청 결과에 대한 명시적 메시지를 제공할 때 활용됩니다.
 *
 * ✅ 예시 응답 형태:
 * {
 *   "message": "채팅방이 성공적으로 생성되었습니다."
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage {

    /**
     * 성공 또는 알림 메시지
     * 예: "삭제되었습니다.", "채팅방이 생성되었습니다.", "변경이 완료되었습니다."
     */
    private String message;
}
