package com.hamcam.back.controller.community.attachment;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.attachment.response.AttachmentListResponse;
import com.hamcam.back.global.exception.BadRequestException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.service.community.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * [AttachmentController]
 * 커뮤니티 게시글의 첨부파일 업로드, 조회, 다운로드, 삭제 기능을 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 게시글 첨부파일 업로드
     *
     * @param postId 업로드 대상 게시글 ID
     * @param files  업로드할 파일들
     * @return 업로드 결과 메시지
     */
    @PostMapping("/posts/{postId}/attachments")
    public ResponseEntity<MessageResponse> uploadPostAttachments(
            @PathVariable Long postId,
            @RequestParam("files") MultipartFile[] files
    ) {
        if (files == null || files.length == 0) {
            throw new BadRequestException(ErrorCode.MISSING_PARAMETER);
        }

        int uploaded = attachmentService.uploadPostFiles(postId, files);
        return ResponseEntity.ok(MessageResponse.of("첨부파일이 업로드되었습니다. (" + uploaded + "개)"));
    }

    /**
     * 게시글 첨부파일 목록 조회
     *
     * @param postId 대상 게시글 ID
     * @return 첨부파일 리스트
     */
    @GetMapping("/posts/{postId}/attachments")
    public ResponseEntity<AttachmentListResponse> getPostAttachments(@PathVariable Long postId) {
        return ResponseEntity.ok(attachmentService.getPostAttachments(postId));
    }

    /**
     * 첨부파일 다운로드
     *
     * @param attachmentId 파일 ID
     * @return 파일 다운로드 응답
     */
    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.downloadAttachment(attachmentId);
        if (resource == null || !resource.exists()) {
            throw new BadRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String filename = resource.getFilename();
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    /**
     * 첨부파일 삭제
     *
     * @param attachmentId 삭제 대상 파일 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<MessageResponse> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(MessageResponse.of("첨부파일이 삭제되었습니다."));
    }
}
