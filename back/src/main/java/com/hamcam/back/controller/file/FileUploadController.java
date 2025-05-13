package com.hamcam.back.controller.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 로컬 파일 업로드 컨트롤러
 * - S3 없이 로컬 서버 디렉토리에 파일 저장
 * - 이미지, 파일 등 첨부 시 저장된 파일명을 반환함
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String UPLOAD_DIR = "C:/upload"; // ✅ 로컬에 직접 생성해둘 것

    /**
     * 로컬 파일 업로드
     *
     * @param file Multipart 파일
     * @return 저장된 파일명
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;

            File dest = new File(UPLOAD_DIR, storedFilename);
            file.transferTo(dest);

            return ResponseEntity.ok(storedFilename); // 채팅 전송 시 사용됨
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 업로드 실패: " + e.getMessage());
        }
    }
}
