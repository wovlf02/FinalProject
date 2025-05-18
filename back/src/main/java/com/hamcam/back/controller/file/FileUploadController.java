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
 * 로컬 환경에서 파일 업로드 처리
 * - 파일은 지정된 로컬 경로(C:/upload)에 저장됨
 * - 저장된 파일명은 UUID를 붙여 중복 방지
 * - 저장 결과는 표준 MessageResponse로 반환
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String UPLOAD_DIR = "C:/upload"; // ✅ 실제 서버/로컬에 해당 폴더가 존재해야 함

    /**
     * [파일 업로드]
     * MultipartFile을 받아 로컬에 저장하고 저장된 파일명을 반환
     *
     * @param file Multipart 파일
     * @return 저장된 파일명 포함 성공 메시지
     */
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 디렉토리 없으면 생성
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.internalServerError().body(
                        MessageResponse.of("업로드 디렉토리 생성 실패"));
            }

            // 파일명 null 방지 처리
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "파일명이 없습니다.");
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;

            File dest = new File(UPLOAD_DIR, storedFilename);
            file.transferTo(dest);

            return ResponseEntity.ok(MessageResponse.of("파일이 업로드되었습니다.", storedFilename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    MessageResponse.of("파일 업로드 중 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    MessageResponse.of("알 수 없는 오류로 업로드에 실패했습니다."));
        }
    }
}
