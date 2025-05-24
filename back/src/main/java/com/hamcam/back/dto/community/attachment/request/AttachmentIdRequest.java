package com.hamcam.back.dto.community.attachment.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [AttachmentIdRequest]
 * 첨부파일 다운로드 및 삭제에 사용하는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentIdRequest {

    @NotNull(message = "attachmentId는 필수입니다.")
    private Long attachmentId;
}
