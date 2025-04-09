package com.hamcam.back.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [ChatMessageResponse]
 *
 * 클라이언트가 채팅방에서 메시지를 수신할 때 전달되는 응답 DTO
 * 텍스트 또는 파일 메시지 모두를 처리할 수 있으며,
 * 발신자 정보, 메시지 전송 시각, 메시지 타입 등을 포함
 *
 * WebSocket 응답 및 REST API 응답에서 사용됨
 *
 * [연관 테이블]
 * CHAT_MESSAGES
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    /**
     * 메시지 고유 ID
     * 데이터베이스에 저장된 메시지의 고유 식별자
     */
    private Long messageId;

    /**
     * 채팅방 ID
     * 어떤 채팅방에 속한 메시지인지 나타냄
     */
    private Long roomId;

    /**
     * 발신자 사용자 ID
     * 메시지를 보낸 사용자 ID
     */
    private Long senderId;

    /**
     * 발신자 닉네임
     * 채팅에 표시될 이름
     */
    private String senderNickname;

    /**
     * 메시지 내용 (텍스트)
     * 일반적인 채팅 메시지 내용
     * 파일 전송 시 NULL 가능
     */
    private String content;

    /**
     * 첨부파일 Base64 또는 URL (옵션)
     * 프론트에서 표시용으로 사용
     * 저장 방식에 따라 파일 데이터 또는 다운로드 링크 제공
     */
    private String fileUrl;

    /**
     * 첨부파일 MIME 타입
     * ex) image/png, application/pdf 등
     * 텍스트 메시지일 경우 null
     */
    private String fileType;

    /**
     * 메시지 전송 시각
     * 메시지가 서버에 저장된 시간
     */
    private LocalDateTime sendAt;
}
