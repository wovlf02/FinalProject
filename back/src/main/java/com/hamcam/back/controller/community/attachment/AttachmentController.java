package com.hamcam.back.controller.community.attachment;

import com.hamcam.back.dto.community.attachment.response.AttachmentListResponse;
import com.hamcam.back.service.community.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.hamcam.back.dto.common.MessageResponse;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 게시글 첨부파일 업로드
     */
    @PostMapping("/posts/{postId}/attachments")
    public ResponseEntity<MessageResponse> uploadPostAttachments(
            @PathVariable Long postId,
            @RequestParam("files") MultipartFile[] files
    ) {
        int uploaded = attachmentService.uploadPostFiles(postId, files);
        return ResponseEntity.ok(new MessageResponse("첨부파일이 업로드되었습니다. (" + uploaded + "개)"));
    }

    /**
     * 댓글 첨부파일 업로드
     */
    @PostMapping("/comments/{commentId}/attachments")
    public ResponseEntity<MessageResponse> uploadCommentAttachments(
            @PathVariable Long commentId,
            @RequestParam("files") MultipartFile[] files
    ) {
        int uploaded = attachmentService.uploadCommentFiles(commentId, files);
        return ResponseEntity.ok(new MessageResponse("첨부파일이 업로드되었습니다. (" + uploaded + "개)"));
    }

    /**
     * 대댓글 첨부파일 업로드
     */
    @PostMapping("/replies/{replyId}/attachments")
    public ResponseEntity<MessageResponse> uploadReplyAttachments(
            @PathVariable Long replyId,
            @RequestParam("files") MultipartFile[] files
    ) {
        int uploaded = attachmentService.uploadReplyFiles(replyId, files);
        return ResponseEntity.ok(new MessageResponse("첨부파일이 업로드되었습니다. (" + uploaded + "개)"));
    }

    /**
     * 게시글 첨부파일 목록 조회
     */
    @GetMapping("/posts/{postId}/attachments")
    public ResponseEntity<AttachmentListResponse> getPostAttachments(@PathVariable Long postId) {
        return ResponseEntity.ok(attachmentService.getPostAttachments(postId));
    }

    /**
     * 댓글 첨부파일 목록 조회
     */
    @GetMapping("/comments/{commentId}/attachments")
    public ResponseEntity<AttachmentListResponse> getCommentAttachments(@PathVariable Long commentId) {
        return ResponseEntity.ok(attachmentService.getCommentAttachments(commentId));
    }

    /**
     * 대댓글 첨부파일 목록 조회
     */
    @GetMapping("/replies/{replyId}/attachments")
    public ResponseEntity<AttachmentListResponse> getReplyAttachments(@PathVariable Long replyId) {
        return ResponseEntity.ok(attachmentService.getReplyAttachments(replyId));
    }

    /**
     * 첨부파일 다운로드
     */
    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.downloadAttachment(attachmentId);
        String filename = resource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * 첨부파일 삭제 (작성자만 가능)
     */
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<MessageResponse> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(new MessageResponse("첨부파일이 삭제되었습니다."));
    }
}
