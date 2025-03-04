package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 * 사용자가 메시지를 조회할 때 사용됨
 */
@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {

    /**
     * 메시지 ID
     * 데이터베이스에서 자동 생성됨
     */
    private Long id;

    /**
     * 채팅방 ID
     * 메시지가 속한 채팅방 ID
     */
    private Long chatRoomId;

    /**
     * 보낸 사용자 ID
     * 메시지를 보낸 사용자의 ID
     */
    private Long senderId;

    /**
     * 메시지 내용
     * 텍스트 기반 메시지
     */
    private String message;

    /**
     * 첨부파일 URL (선택 사항)
     * 이미지, 문서, 동영상 등의 파일 URL
     */
    private String attachmentUrl;

    /**
     * 메시지 전송 시각
     * 메시지가 전송된 시각
     */
    private LocalDateTime sendAt;
}
