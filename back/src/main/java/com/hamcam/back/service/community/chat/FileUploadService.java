package com.hamcam.back.service.community.chat;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final String CHATROOM_IMAGE_DIR = "uploads/chatroom/";
    private static final Path UPLOAD_BASE_PATH = Paths.get(CHATROOM_IMAGE_DIR);

    /**
     * 채팅방 대표 이미지 저장
     *
     * @param file 업로드된 MultipartFile
     * @return 저장된 웹 경로 (ex: "/uploads/chatroom/uuid_name.jpg")
     */
    public String storeChatRoomImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            ensureDirectoryExists(UPLOAD_BASE_PATH);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "파일명이 유효하지 않습니다.");
            }

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = UPLOAD_BASE_PATH.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/chatroom/" + storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "파일 업로드 실패", e);
        }
    }

    /**
     * 채팅방 대표 이미지 삭제
     *
     * @param storedPath "/uploads/chatroom/uuid_파일명" 혹은 순수 파일명
     */
    public void deleteChatRoomImage(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return;

        try {
            String fileName = extractFileName(storedPath);
            Path filePath = UPLOAD_BASE_PATH.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED, "대표 이미지 삭제 중 오류 발생", e);
        }
    }

    /**
     * 이미지 미리보기 가능 여부
     */
    public boolean isImagePreviewable(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.matches(".*(jpg|jpeg|png|gif|bmp|webp)$") || lower.startsWith("image/");
    }

    // ===== 유틸 메서드 =====

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
