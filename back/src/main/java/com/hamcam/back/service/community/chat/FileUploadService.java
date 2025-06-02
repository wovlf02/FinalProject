package com.hamcam.back.service.community.chat;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * [FileUploadService]
 *
 * 채팅방 대표 이미지 업로드 및 삭제 유틸 서비스
 * 컨트롤러와 직접 연동되어 파일 저장 및 미리보기 지원
 */
@Service
public class FileUploadService {

    private static final String CHATROOM_IMAGE_DIR = "uploads/chatroom/";
    private static final Path UPLOAD_BASE_PATH = Paths.get(CHATROOM_IMAGE_DIR);

    /**
     * 채팅방 대표 이미지 저장
     *
     * @param file Multipart 업로드 파일
     * @return 저장된 이미지의 URL 경로
     */
    public String storeChatRoomImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            ensureDirectoryExists(UPLOAD_BASE_PATH);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = UPLOAD_BASE_PATH.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/chatroom/" + storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 채팅방 대표 이미지 삭제
     *
     * @param storedPath 저장된 경로 또는 파일명
     */
    public void deleteChatRoomImage(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return;

        try {
            String fileName = extractFileName(storedPath);
            Path filePath = UPLOAD_BASE_PATH.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * 확장자 기준 미리보기 가능 여부 확인
     */
    public boolean isImagePreviewable(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.matches(".*(jpg|jpeg|png|gif|bmp|webp)$") || lower.startsWith("image/");
    }

    // ===== 유틸 =====

    private void ensureDirectoryExists(Path dir) throws IOException {
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
    }

    private String extractFileName(String fullPath) {
        return fullPath.contains("/") ?
                fullPath.substring(fullPath.lastIndexOf("/") + 1) :
                fullPath;
    }
}
