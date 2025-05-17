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

    /**
     * 채팅방 대표 이미지 파일을 저장하고 저장된 파일명을 반환합니다.
     *
     * @param file 업로드된 MultipartFile
     * @return 저장된 파일명
     */
    public String storeChatRoomImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path dirPath = Paths.get(CHATROOM_IMAGE_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = dirPath.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 저장된 대표 이미지 파일을 삭제합니다.
     *
     * @param storedFilename 저장된 파일명
     */
    public void deleteChatRoomImage(String storedFilename) {
        try {
            if (storedFilename == null || storedFilename.isBlank()) return;
            Path filePath = Paths.get(CHATROOM_IMAGE_DIR).resolve(storedFilename).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("대표 이미지 삭제 중 오류 발생", e);
        }
    }

    /**
     * 미리보기 가능한 이미지 파일인지 확인합니다.
     *
     * @param filename 파일명 또는 content-type
     * @return boolean
     */
    public boolean isImagePreviewable(String filename) {
        String lower = filename.toLowerCase();
        return lower.matches(".*(jpg|jpeg|png|gif|bmp|webp)$");
    }
}
