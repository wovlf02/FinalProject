package com.hamcam.back.controller.community.chat;

import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.global.exception.BadRequestException;
import com.hamcam.back.global.exception.ErrorCode;
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
     *
     * @param messageId 파일이 첨부된 메시지 ID
     * @return 파일 다운로드 응답
     */
    @GetMapping("/{messageId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageId) {
        Resource fileResource = chatAttachmentService.loadFileAsResource(messageId);
        if (fileResource == null || !fileResource.exists()) {
            throw new BadRequestException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String encodedFilename = URLEncoder.encode(fileResource.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(fileResource);
    }

    /**
     * 채팅 이미지 파일 미리보기 정보 조회
     *
     * @param messageId 이미지가 첨부된 메시지 ID
     * @return 이미지 미리보기 URL 등 정보
     */
    @GetMapping("/{messageId}/preview")
    public ResponseEntity<ChatFilePreviewResponse> previewImage(@PathVariable Long messageId) {
        ChatFilePreviewResponse preview = chatAttachmentService.previewFile(messageId);
        return ResponseEntity.ok(preview);
    }

    /**
     * 채팅 파일 업로드 (REST 방식)
     *
     * @param roomId 채팅방 ID
     * @param file   업로드할 파일
     * @return 업로드된 파일이 포함된 메시지 정보
     */
    @PostMapping("/{roomId}/upload")
    public ResponseEntity<ChatMessageResponse> uploadFileMessage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorCode.MISSING_PARAMETER);
        }

        ChatMessageResponse response = chatAttachmentService.saveFileMessage(roomId, file);
        return ResponseEntity.ok(response);
    }
}
