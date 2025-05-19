package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatMessageRequest]
 *
 * WebSocket 또는 REST 방식으로 채팅 메시지를 전송할 때 사용하는 요청 DTO입니다.
 * 텍스트, 이미지, 파일 메시지를 구분하여 처리할 수 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageRequest {

    /**
     * 메시지를 보낸 사용자 ID (WebSocket 인증 토큰 기반이면 생략 가능)
     */
    private Long senderId;

    /**
     * 메시지가 전송되는 채팅방 ID
     */
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    /**
     * 메시지 본문 (텍스트 or 파일명)
     */
    private String content;

    /**
     * 메시지 타입: TEXT | IMAGE | FILE 등
     */
    @NotNull(message = "메시지 타입은 필수입니다.")
    private String type;

    /**
     * 서버에 저장된 파일명 (파일 메시지일 경우만 사용)
     */
    private String storedFileName;
}
