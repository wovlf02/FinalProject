package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 첨부파일 단건 응답 DTO
 * <p>
 * 게시글, 댓글, 대댓글에 연결된 개별 첨부파일 정보를 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
@Builder
public class AttachmentResponse {

    /**
     * 첨부파일 고유 ID
     */
    private Long attachmentId;

    /**
     * 사용자가 업로드한 원본 파일명
     */
    private String originalName;

    /**
     * 서버에 저장된 파일명 (UUID 포함)
     */
    private String storedName;

    /**
     * MIME 타입 (예: image/png, application/pdf 등)
     */
    private String contentType;

    /**
     * 이미지 미리보기 가능 여부 (프론트에서 썸네일 지원 시 사용)
     */
    private boolean previewAvailable;
}
