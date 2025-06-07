package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyChatMessageResponse {
    private String roomId;          // 방 ID (예: focus-1, quiz-3)
    private Long senderId;          // 전송자 userId
    private String nickname;        // 전송자 닉네임
    private String profileUrl;      // 프로필 이미지 URL (없으면 null 허용)
    private String content;         // 텍스트 메시지
    private LocalDateTime sentAt;   // 전송 시간
}
