package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ChatFilePreviewResponse]
 *
 * 채팅 첨부 이미지 미리보기 응답 DTO입니다.
 * 클라이언트가 base64 형식의 이미지 데이터를 이용해 즉시 미리보기를 렌더링할 수 있도록 제공합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatFilePreviewResponse {

    /**
     * 이미지 MIME 타입 (예: image/png, image/jpeg 등)
     */
    private String contentType;

    /**
     * base64 인코딩된 이미지 데이터 문자열
     */
    private String base64Data;
}
