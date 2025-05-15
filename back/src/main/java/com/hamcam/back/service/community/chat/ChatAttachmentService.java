package com.hamcam.back.service.community.chat;

import com.hamcam.back.dto.community.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.dto.community.chat.response.ChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.entity.chat.ChatRoom;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatAttachmentService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SecurityUtil securityUtil;

    private static final String UPLOAD_DIR = "C:/FinalProject/uploads/chat";
    private static final String BASE_FILE_URL = "/uploads/chat";


    /**
     * 파일 다운로드 리소스 반환
     *
     * @param messageId 파일이 첨부된 메시지 ID
     * @return 다운로드용 파일 리소스
     */
    public Resource loadFileAsResource(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 첨부된 메시지를 찾을 수 없습니다."));

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(message.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("파일이 존재하지 않거나 읽을 수 없습니다.");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류", e);
        }
    }

    /**
     * 이미지 파일 base64 미리보기 생성
     *
     * @param messageId 이미지가 첨부된 메시지 ID
     * @return MIME 타입과 base64 인코딩된 데이터
     */
    public ChatFilePreviewResponse previewFile(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("파일 메시지를 찾을 수 없습니다."));

        String storedFileName = message.getStoredFileName();
        String extension = getFileExtension(storedFileName).toLowerCase();

        if (!isPreviewable(extension)) {
            throw new IllegalArgumentException("미리보기가 지원되지 않는 파일 형식입니다.");
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(storedFileName).normalize();
            byte[] fileBytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            String mimeType = Files.probeContentType(filePath);

            return new ChatFilePreviewResponse(mimeType, base64);
        } catch (IOException e) {
            throw new RuntimeException("미리보기 생성 중 오류 발생", e);
        }
    }

    /**
     * 파일 메시지 업로드 및 메시지 저장
     */
    public ChatMessageResponse saveFileMessage(Long roomId, MultipartFile file) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User sender = securityUtil.getCurrentUser(); // ✅ 현재 로그인 사용자

        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        File savePath = new File(UPLOAD_DIR, storedFilename);

        if (!savePath.getParentFile().exists()) {
            savePath.getParentFile().mkdirs();
        }

        try {
            file.transferTo(savePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender) // ✅ 인증된 사용자로 설정
                .type("FILE")
                .content(originalFilename)
                .storedFileName(storedFilename)
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
        return toResponse(message);
    }

    /**
     * 확장자 추출
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex != -1) ? filename.substring(dotIndex + 1) : "";
    }

    /**
     * 미리보기 가능한 확장자 목록
     */
    private boolean isPreviewable(String ext) {
        return switch (ext) {
            case "png", "jpg", "jpeg", "gif", "bmp", "webp" -> true;
            default -> false;
        };
    }

    /**
     * ChatMessage → ChatMessageResponse 변환
     */
    private ChatMessageResponse toResponse(ChatMessage message) {
        User sender = message.getSender();
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(sender.getId())
                .content(message.getContent())
                .type(message.getType())
                .storedFileName(message.getStoredFileName())
                .sentAt(message.getSentAt())
                .profileUrl(sender.getProfileImageUrl()) // null 가능성 유의
                .nickname(sender.getNickname())
                .build();
    }
}
