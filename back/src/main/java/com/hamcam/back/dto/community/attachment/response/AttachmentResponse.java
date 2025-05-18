package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * [AttachmentResponse]
 *
 * 게시글, 댓글, 대댓글 등에 연결된 단일 첨부파일에 대한 응답 DTO입니다.
 * 프론트엔드에서 파일명, MIME 타입, 미리보기 여부 등을 표시하는 데 사용됩니다.
 */
@Getter
@Builder
@AllArgsConstructor
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
     * 서버에 저장된 파일명 (예: UUID_원본명)
     */
    private String storedName;

    /**
     * MIME 타입 (예: image/png, application/pdf 등)
     */
    private String contentType;

    /**
     * 이미지 미리보기 가능 여부 (썸네일 표시용)
     */
    private boolean previewAvailable;
}
