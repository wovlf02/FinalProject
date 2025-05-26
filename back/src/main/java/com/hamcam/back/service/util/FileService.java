package com.hamcam.back.service.util;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String PROFILE_BASE_DIR = "uploads/profile/";

    private final UserRepository userRepository;

    /**
     * ✅ 프로필 이미지 저장 (세션 기반)
     */
    public String saveProfileImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 사용자별 디렉토리 설정
            Path userDir = Paths.get(PROFILE_BASE_DIR + userId);
            if (Files.notExists(userDir)) {
                Files.createDirectories(userDir);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetPath = userDir.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 프론트 접근용 경로 반환
            return "/uploads/profile/" + userId + "/" + storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * ✅ 프로필 이미지 삭제 (세션 기반)
     */
    public void deleteProfileImage(String storedPath, HttpServletRequest request) {
        try {
            if (storedPath == null || storedPath.isBlank()) return;

            Long userId = SessionUtil.getUserId(request);
            String filename = storedPath.contains("/") ?
                    storedPath.substring(storedPath.lastIndexOf("/") + 1) :
                    storedPath;

            Path path = Paths.get(PROFILE_BASE_DIR + userId).resolve(filename);
            Files.deleteIfExists(path);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }
}
