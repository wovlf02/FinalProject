package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * [ChatAttachmentController]
 * 채팅 파일 첨부 관련 API 컨트롤러
 * - 파일 업로드, 다운로드, 미리보기 기능 제공
 */
@RestController
@RequestMapping("/api/chat/files")
@RequiredArgsConstructor
public class ChatAttachmentController {

    private final ChatAttachmentService chatAttachmentService;

    /**
     * 채팅 파일 다운로드
     */
    @GetMapping("/{messageId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageId) {
        Resource resource = chatAttachmentService.loadFileAsResource(messageId);

        String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    /**
     * 채팅 이미지 미리보기
     */
    @GetMapping("/{messageId}/preview")
    public ResponseEntity<ChatFilePreviewResponse> previewImage(@PathVariable Long messageId) {
        return ResponseEntity.ok(chatAttachmentService.previewFile(messageId));
    }

    /**
     * 채팅 파일 업로드
     */
    @PostMapping("/{roomId}/upload")
    public ResponseEntity<ChatMessageResponse> uploadFileMessage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(chatAttachmentService.saveFileMessage(roomId, file));
    }
}
