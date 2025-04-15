package com.hamcam.back.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 첨부 이미지 미리보기 응답 DTO
 * 프론트에서 base64 형식으로 렌더링할 수 있음
 */
@Data
@AllArgsConstructor
public class ChatFilePreviewResponse {

    /**
     * 파일의 MIME 타입 (예: image/png)
     */
    private String contentType;

    /**
     * base64 인코딩된 이미지 데이터
     */
    private String base64Data;

    /**
     * 이미지 너비
     */
    private int width;

    /**
     * 이미지 높이
     */
    private int height;
}
