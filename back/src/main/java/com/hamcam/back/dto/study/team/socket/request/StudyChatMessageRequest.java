package com.hamcam.back.dto.study.team.socket.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ StudyRoom(FocusRoom, QuizRoom) 전용 채팅 메시지 전송 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyChatMessageRequest {
    private Long roomId;      // 방 ID (숫자 형태, 예: 1)
    private String content;   // 채팅 메시지 내용 (텍스트)
}
