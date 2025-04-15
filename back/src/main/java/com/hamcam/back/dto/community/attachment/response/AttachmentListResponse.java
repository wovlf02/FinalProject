package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 첨부파일 목록 응답 DTO
 * <p>
 * 게시글, 댓글, 대댓글에 첨부된 파일들의 목록을 반환할 때 사용됩니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class AttachmentListResponse {

    /**
     * 첨부파일 응답 리스트
     */
    private List<AttachmentDownloadResponse> attachments;
}
