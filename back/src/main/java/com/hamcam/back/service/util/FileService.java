package com.hamcam.back.service.util;

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
public class FileService {

    private static final String PROFILE_UPLOAD_DIR = "uploads/profile/";

    /**
     * 프로필 이미지 저장 메서드
     *
     * @param file 업로드된 이미지 파일
     * @return 저장된 파일의 웹 접근 경로 (/uploads/profile/...)
     */
    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path dirPath = Paths.get(PROFILE_UPLOAD_DIR);
            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = dirPath.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ 프론트에서 접근 가능한 상대 경로 리턴
            return "/uploads/profile/" + storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 프로필 이미지 삭제
     *
     * @param storedPath 저장된 웹 경로 또는 파일명
     */
    public void deleteProfileImage(String storedPath) {
        try {
            if (storedPath == null || storedPath.isBlank()) return;

            String filename = storedPath.contains("/") ?
                    storedPath.substring(storedPath.lastIndexOf("/") + 1) :
                    storedPath;

            Path path = Paths.get(PROFILE_UPLOAD_DIR).resolve(filename);
            Files.deleteIfExists(path);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
}