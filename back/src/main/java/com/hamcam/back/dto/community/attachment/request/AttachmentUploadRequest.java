package com.hamcam.back.dto.community.attachment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * [AttachmentUploadRequest]
 * 게시글 첨부파일 업로드 요청 DTO
 * - Multipart 요청은 @ModelAttribute로 처리
 */
@Getter
@Setter
@NoArgsConstructor
public class AttachmentUploadRequest {

    @NotNull(message = "postId는 필수입니다.")
    private Long postId;

    @NotNull(message = "첨부파일은 필수입니다.")
    private MultipartFile[] files;
}
