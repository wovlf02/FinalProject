package com.hamcam.back.dto.community.attachment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * [AttachmentListResponse]
 *
 * 게시글, 댓글, 대댓글 등에 포함된 첨부파일 목록을 클라이언트에 반환할 때 사용하는 응답 DTO입니다.
 * 각 첨부파일에 대한 상세 정보는 AttachmentResponse로 구성됩니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class AttachmentListResponse {

    /**
     * 첨부파일 응답 리스트
     */
    private List<AttachmentResponse> attachments;
}
