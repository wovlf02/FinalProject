package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 첨부파일 다운로드/미리보기용 응답 DTO
 * <p>
 * 프론트에서 파일명, 다운로드 URL, 미리보기 여부 등을 구성할 수 있도록 제공됩니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class AttachmentDownloadResponse {

    /**
     * 첨부파일 ID
     */
    private Long id;

    /**
     * 원본 파일명 (사용자 업로드명)
     */
    private String originalFileName;

    /**
     * 저장된 파일명 (서버 내부 또는 S3 저장 키 등)
     */
    private String storedFileName;

    /**
     * 다운로드 또는 접근 가능한 파일 URL
     */
    private String url;

    /**
     * MIME 타입 (예: image/png, application/pdf 등)
     */
    private String contentType;

    /**
     * 이미지 미리보기 여부
     */
    private boolean previewAvailable;
}
