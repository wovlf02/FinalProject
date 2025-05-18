package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [ChatMessageResponse]
 *
 * 채팅 메시지 응답 DTO입니다.
 * 텍스트, 이미지, 파일 메시지 등 다양한 메시지 유형에 대응하며,
 * 프론트에서는 메시지 타입에 따라 렌더링 방식을 분기할 수 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    /**
     * 메시지 고유 ID
     */
    private Long messageId;

    /**
     * 채팅방 ID
     */
    private Long roomId;

    /**
     * 보낸 사용자 ID
     */
    private Long senderId;

    /**
     * 보낸 사용자 닉네임
     */
    private String nickname;

    /**
     * 보낸 사용자 프로필 이미지 URL
     */
    private String profileUrl;

    /**
     * 메시지 내용 (텍스트 메시지일 경우 본문, 파일일 경우 파일명)
     */
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE)
     */
    private String type;

    /**
     * 저장된 파일명 (파일/이미지 메시지의 경우 사용)
     */
    private String storedFileName;

    /**
     * 메시지 전송 시각
     */
    private LocalDateTime sentAt;

    /**
     * 아직 이 메시지를 읽지 않은 참여자 수
     */
    private int unreadCount;
}
