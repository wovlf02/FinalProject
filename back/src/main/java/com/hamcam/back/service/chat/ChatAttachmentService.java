package com.hamcam.back.service.chat;

import com.hamcam.back.dto.chat.response.ChatFilePreviewResponse;
import com.hamcam.back.entity.chat.ChatMessage;
import com.hamcam.back.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.nio.file.Files;

/**
 * 채팅 첨부파일 처리 서비스
 * <p>
 * 채팅 메시지에 첨부된 파일의 다운로드 및 미리보기를 제공하는 서비스입니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChatAttachmentService {

    private final ChatMessageRepository chatMessageRepository;

    private final String UPLOAD_DIR = "uploads/chat/"; // 실제 파일 경로 (S3 사용 시 변경)

    /**
     * 채팅 메시지의 첨부파일을 다운로드용으로 불러오기
     *
     * @param messageId 첨부파일이 포함된 메시지 ID
     * @return 파일 리소스
     */
    public Resource loadFileAsResource(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(message.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("파일을 찾을 수 없습니다.");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류", e);
        }
    }

    /**
     * 첨부파일이 이미지일 경우 미리보기 데이터를 base64로 반환
     *
     * @param fileId 미리볼 첨부파일의 메시지 ID
     * @return 이미지 스트리밍용 base64 응답
     */
    public ChatFilePreviewResponse previewFile(Long fileId) {
        ChatMessage message = chatMessageRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일 메시지를 찾을 수 없습니다."));

        String storedFileName = message.getStoredFileName();
        String extension = getFileExtension(storedFileName).toLowerCase();

        // 미리보기 가능한 확장자인지 확인
        if (!isPreviewable(extension)) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(storedFileName).normalize();
            byte[] fileBytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            return new ChatFilePreviewResponse(extension, base64);
        } catch (Exception e) {
            throw new RuntimeException("미리보기 생성 중 오류 발생", e);
        }
    }

    /**
     * 확장자 추출
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * 미리보기 가능한 확장자 여부 확인
     */
    private boolean isPreviewable(String ext) {
        return switch (ext) {
            case "png", "jpg", "jpeg", "gif", "bmp", "webp" -> true;
            default -> false;
        };
    }
}
