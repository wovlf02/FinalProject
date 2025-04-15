package com.hamcam.back.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ResponseMessage]
 *
 * 성공 응답에 사용되는 기본 메시지 DTO 클래스.
 * 클라이언트에게 명확한 메시지를 전달하고,
 * 특정 작업(등록, 삭제, 수정 등)에 대한 결과 알림용으로 사용됨.
 *
 * 예시 응답 형태:
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
     * 클라이언트에게 전달할 성공 메시지
     * ex) "채팅방이 생성되었습니다.", "삭제 완료", "요청이 성공적으로 처리되었습니다."
     */
    private String message;
}
