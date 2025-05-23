package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.request.ChatFileUploadRequest;
import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatMessageType;
import com.hamcam.back.entity.chat.ChatRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
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

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 채팅 파일 업로드 및 메시지 저장 (DTO 기반)
     */
    public ChatMessageResponse saveFileMessage(ChatFileUploadRequest request) {
        MultipartFile file = request.getFile();
        Long roomId = request.getRoomId();
        Long senderId = request.getUserId();

        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.MISSING_PARAMETER);
        }

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File dest = new File(uploadDir, storedFilename);
        try {
            file.transferTo(dest);
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
     * ✅ 채팅 메시지의 파일 다운로드 리소스 반환
     */
    public Resource loadFileAsResource(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(message.getStoredFileName()).normalize();
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
     * ✅ 이미지 파일 미리보기 (Base64)
     */
    public ChatFilePreviewResponse previewFile(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        String filename = message.getStoredFileName();
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();

        String extension = getFileExtension(filename).toLowerCase();
        if (!isPreviewable(extension)) {
            throw new CustomException(ErrorCode.FILE_PREVIEW_NOT_SUPPORTED);
        }

        try {
            byte[] bytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mimeType = Files.probeContentType(filePath);

            return new ChatFilePreviewResponse(mimeType, base64);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_PREVIEW_FAILED);
        }
    }

    // ===== 내부 변환 유틸 =====

    private ChatMessageResponse toResponse(ChatMessage message) {
        User sender = message.getSender();
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender.getId())
                .nickname(sender.getNickname())
                .profileUrl(sender.getProfileImageUrl() != null ? sender.getProfileImageUrl() : "")
                .type(message.getType())
                .content(message.getContent())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .build();
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex != -1 ? filename.substring(dotIndex + 1) : "";
    }

    private boolean isPreviewable(String ext) {
        return switch (ext) {
            case "jpg", "jpeg", "png", "gif", "webp", "bmp" -> true;
            default -> false;
        };
    }
}
