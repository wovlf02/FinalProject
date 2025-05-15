package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 첨부 이미지 미리보기 응답 DTO
 * <p>
 * base64 이미지 + 이미지 크기 정보 제공
 * </p>
 */
@Data
@AllArgsConstructor
public class ChatFilePreviewResponse {

    /**
     * 이미지 MIME 타입 (예: image/png)
     */
    private String contentType;

    /**
     * base64 인코딩된 이미지 데이터
     */
    private String base64Data;
}
