package com.hamcam.back.controller.file;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String BASE_UPLOAD_DIR = "C:/upload";

    /**
     * ✅ 파일 업로드 (세션 기반, 단일 파일)
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest
    ) {
        try {
            Long userId = SessionUtil.getUserId(httpRequest);

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(MessageResponse.of("❌ 업로드할 파일이 없습니다."));
            }

            String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "파일명이 존재하지 않습니다.");
            if (originalFilename.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(MessageResponse.of("❌ 파일명이 유효하지 않습니다."));
            }

            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            File userDir = new File(BASE_UPLOAD_DIR, String.valueOf(userId));
            if (!userDir.exists() && !userDir.mkdirs()) {
                return ResponseEntity.internalServerError()
                        .body(MessageResponse.of("❌ 업로드 경로 생성에 실패했습니다."));
            }

            File dest = new File(userDir, storedFilename);
            file.transferTo(dest);

            String relativePath = "/upload/" + userId + "/" + storedFilename;
            return ResponseEntity.ok(MessageResponse.of("✅ 파일이 업로드되었습니다.", relativePath));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.of("❌ 파일 저장 중 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.of("❌ 알 수 없는 오류로 업로드에 실패했습니다."));
        }
    }
}
