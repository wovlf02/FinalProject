package com.hamcam.back.service.util;

import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String PROFILE_BASE_DIR = "uploads/profile/";
    private static final String STUDY_BASE_DIR = "uploads/study/";

    private final UserRepository userRepository;

    // ----------------------------------
    // ✅ 기존: 프로필 업로드
    // ----------------------------------

    public String saveProfileImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) return null;
        return saveFile(file, userId, PROFILE_BASE_DIR);
    }

    public void deleteProfileImage(String storedPath, HttpServletRequest request) {
        deleteFile(storedPath, SessionUtil.getUserId(request), PROFILE_BASE_DIR);
    }

    // ----------------------------------
    // ✅ 추가: 팀 학습 파일 업로드
    // ----------------------------------

    public String saveStudyFile(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return saveFile(file, userId, STUDY_BASE_DIR);
    }

    // ----------------------------------
    // ✅ 공통 파일 저장 메서드
    // ----------------------------------

    private String saveFile(MultipartFile file, Long userId, String baseDir) {
        try {
            Path userDir = Paths.get(baseDir + userId);
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

            // 프론트 접근 경로 반환
            return "/" + baseDir + userId + "/" + storedFilename;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // ----------------------------------
    // ✅ 공통 파일 삭제 메서드
    // ----------------------------------

    private void deleteFile(String storedPath, Long userId, String baseDir) {
        try {
            if (storedPath == null || storedPath.isBlank()) return;

            String filename = storedPath.contains("/") ?
                    storedPath.substring(storedPath.lastIndexOf("/") + 1) :
                    storedPath;

            Path path = Paths.get(baseDir + userId).resolve(filename);
            Files.deleteIfExists(path);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    // ----------------------------------
    // ✅ 스터디룸 파일 조회
    // ----------------------------------
    public List<String> getStudyFileList(Long roomId) {
        // 예: uploads/study/room-123/
        String dirPath = STUDY_BASE_DIR + "room-" + roomId;
        File dir = new File(dirPath);

        if (!dir.exists() || !dir.isDirectory()) {
            return List.of(); // 디렉토리가 없으면 빈 리스트 반환
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return List.of();
        }

        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add("/" + dirPath + "/" + file.getName()); // 프론트 접근 경로 기준
            }
        }

        return fileNames;
    }
}
