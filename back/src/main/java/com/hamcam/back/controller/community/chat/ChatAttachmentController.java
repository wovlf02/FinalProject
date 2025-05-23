package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatFileUploadRequest;
import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * [ChatAttachmentController]
 * 채팅방 파일 첨부 관련 REST API
 * - 파일 업로드, 다운로드, 이미지 미리보기 처리
 */
@RestController
@RequestMapping("/api/chat/files")
@RequiredArgsConstructor
public class ChatAttachmentController {

    private final ChatAttachmentService chatAttachmentService;

    /**
     * ✅ 파일 업로드 메시지 전송
     * - Multipart/form-data 기반 요청
     */
    @PostMapping("/upload")
    public ResponseEntity<ChatMessageResponse> uploadFileMessage(
            @ModelAttribute ChatFileUploadRequest request) {

        return ResponseEntity.ok(chatAttachmentService.saveFileMessage(request));
    }

    /**
     * ✅ 이미지 미리보기 (base64 반환)
     */
    @GetMapping("/{messageId}/preview")
    public ResponseEntity<ChatFilePreviewResponse> previewImage(
            @PathVariable Long messageId) {

        return ResponseEntity.ok(chatAttachmentService.previewFile(messageId));
    }

    /**
     * ✅ 파일 다운로드
     */
    @GetMapping("/{messageId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long messageId) {

        Resource resource = chatAttachmentService.loadFileAsResource(messageId);
        String filename = resource.getFilename();

        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }
}
