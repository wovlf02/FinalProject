package com.hamcam.back.controller.chat;

import com.hamcam.back.dto.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.service.chat.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
public class ChatAttachmentController {

    private final ChatAttachmentService chatAttachmentService;

    /**
     * 채팅 메시지에 첨부된 파일 다운로드
     *
     * @param messageId 첨부파일이 포함된 메시지 ID
     * @return 바이너리 파일 다운로드 응답
     */
    @GetMapping("/{messageId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageId) {
        Resource fileResource = chatAttachmentService.loadFileAsResource(messageId);
        String filename = fileResource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileResource);
    }

    /**
     * 첨부 이미지 파일 미리보기 스트리밍
     *
     * @param fileId 미리보기 대상 파일 ID
     * @return 이미지 미리보기용 응답 (base64 or stream)
     */
    @GetMapping("/files/{fileId}")
    public ResponseEntity<ChatFilePreviewResponse> previewImage(@PathVariable Long fileId) {
        ChatFilePreviewResponse preview = chatAttachmentService.previewFile(fileId);
        return ResponseEntity.ok(preview);
    }
}
