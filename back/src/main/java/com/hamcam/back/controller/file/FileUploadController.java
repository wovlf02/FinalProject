package com.hamcam.back.controller.file;

import com.hamcam.back.dto.common.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * [FileUploadController]
 *
 * 로컬 환경에서 파일 업로드 처리 (userId 기반 확장 적용)
 * - 저장 경로: C:/upload/{userId}/
 * - 파일명 중복 방지: UUID 추가
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String BASE_UPLOAD_DIR = "C:/upload"; // ✅ 실제 폴더 존재해야 함

    /**
     * 파일 업로드 (userId 하위 경로에 저장)
     */
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // 파일명 null 검사
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "파일명이 없습니다.");
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;

            // userId별 업로드 디렉토리 생성
            File userDir = new File(BASE_UPLOAD_DIR + "/" + userId);
            if (!userDir.exists() && !userDir.mkdirs()) {
                return ResponseEntity.internalServerError().body(
                        MessageResponse.of("업로드 디렉토리 생성 실패"));
            }

            // 저장
            File dest = new File(userDir, storedFilename);
            file.transferTo(dest);

            String relativePath = "/upload/" + userId + "/" + storedFilename;
            return ResponseEntity.ok(MessageResponse.of("파일이 업로드되었습니다.", relativePath));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    MessageResponse.of("파일 업로드 중 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    MessageResponse.of("알 수 없는 오류로 업로드에 실패했습니다."));
        }
    }
}
