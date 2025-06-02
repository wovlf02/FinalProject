package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ✅ 텍스트 공지 응답 DTO
 * ex) 발표 성공/실패 메시지
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextNoticeResponse {
    private String message;
}
