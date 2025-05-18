package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 첨부파일 다운로드 및 미리보기에 사용되는 응답 DTO
 * <p>
 * - 프론트엔드는 이 DTO를 기반으로 파일명을 표시하거나,
 *   이미지 미리보기를 수행하고 다운로드 링크를 제공할 수 있습니다.
 * </p>
 */
@Getter
@Builder
@AllArgsConstructor
public class AttachmentDownloadResponse {

    /**
     * 첨부파일 고유 ID
     */
    private Long id;

    /**
     * 사용자에게 보이는 업로드 당시의 원본 파일명
     */
    private String originalFileName;

    /**
     * 서버에 저장된 파일 이름 (UUID 포함, 중복 방지 목적)
     */
    private String storedFileName;

    /**
     * 실제 접근 가능한 파일 URL
     */
    private String url;

    /**
     * MIME 타입 (예: image/png, application/pdf 등)
     */
    private String contentType;

    /**
     * 이미지 미리보기 가능 여부 (true: 이미지로 렌더링 가능)
     */
    private boolean previewAvailable;
}
