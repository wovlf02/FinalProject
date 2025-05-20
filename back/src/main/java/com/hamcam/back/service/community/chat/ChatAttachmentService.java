package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import com.hamcam.back.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatAttachmentService {

    private static final String UPLOAD_DIR = "C:/FinalProject/uploads/chat";
    private static final String BASE_FILE_URL = "/uploads/chat";

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SecurityUtil securityUtil;

    /**
     * 채팅 파일 업로드 및 메시지 저장
     */
    public ChatMessageResponse saveFileMessage(Long roomId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_PARAMETER);
        }

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User sender = securityUtil.getCurrentUser();

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        String storedFilename = generateStoredFilename(originalFilename);
        ensureUploadDirectoryExists();

        File destination = new File(UPLOAD_DIR, storedFilename);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .type(ChatMessageType.FILE)
                .content(originalFilename)
                .storedFileName(storedFilename)
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
        return toResponse(message);
    }

    /**
     * 채팅 파일 다운로드용 리소스 반환
     */
    public Resource loadFileAsResource(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        try {
            Path filePath = resolveFilePath(message.getStoredFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    /**
     * 이미지 파일 Base64 미리보기
     */
    public ChatFilePreviewResponse previewFile(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        String filename = message.getStoredFileName();
        String extension = getFileExtension(filename).toLowerCase();

        if (!isPreviewable(extension)) {
            throw new CustomException(ErrorCode.FILE_PREVIEW_NOT_SUPPORTED);
        }

        try {
            Path filePath = resolveFilePath(filename);
            byte[] bytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mimeType = Files.probeContentType(filePath);

            return new ChatFilePreviewResponse(mimeType, base64);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_PREVIEW_FAILED);
        }
    }

    // ===== 유틸 =====

    private ChatMessageResponse toResponse(ChatMessage message) {
        User sender = message.getSender();

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender.getId())
                .content(message.getContent())
                .type(message.getType().name())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .nickname(sender.getNickname())
                .profileUrl(sender.getProfileImageUrl() != null ? sender.getProfileImageUrl() : "")
                .build();
    }

    private Path resolveFilePath(String storedFilename) {
        return Paths.get(UPLOAD_DIR).resolve(storedFilename).normalize();
    }

    private String generateStoredFilename(String original) {
        return UUID.randomUUID() + "_" + original;
    }

    private void ensureUploadDirectoryExists() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot != -1) ? filename.substring(dot + 1) : "";
    }

    private boolean isPreviewable(String ext) {
        return switch (ext) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp" -> true;
            default -> false;
        };
    }
}
