package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatAttachmentRequest;
import com.hamcam.back.dto.community.chat.request.ChatFileUploadRequest;
import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.service.community.chat.ChatAttachmentService;
import jakarta.servlet.http.HttpServletRequest;
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
     * ✅ 파일 업로드 메시지 전송 (세션 기반 사용자)
     * - Multipart 파일 포함 → @ModelAttribute 사용
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatMessageResponse> uploadFileMessage(
            @ModelAttribute ChatFileUploadRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(chatAttachmentService.saveFileMessage(request, httpRequest));
    }

    /**
     * ✅ 이미지 미리보기 (Base64 인코딩된 결과 반환)
     */
    @PostMapping("/preview")
    public ResponseEntity<ChatFilePreviewResponse> previewImage(
            @RequestBody ChatAttachmentRequest request
    ) {
        return ResponseEntity.ok(chatAttachmentService.previewFile(request.getMessageId()));
    }

    /**
     * ✅ 파일 다운로드
     */
    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestBody ChatAttachmentRequest request
    ) {
        Resource resource = chatAttachmentService.loadFileAsResource(request.getMessageId());
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
