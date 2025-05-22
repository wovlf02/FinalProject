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
 * 로컬 환경에서 파일 업로드를 처리하는 컨트롤러입니다.
 * - 파일 저장 위치: C:/upload/{userId}/
 * - 파일명 중복 방지를 위해 UUID를 파일명에 포함합니다.
 * - 응답으로 상대경로(`/upload/{userId}/{filename}`)를 반환합니다.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String BASE_UPLOAD_DIR = "C:/upload";

    /**
     * 파일 업로드
     *
     * @param userId 업로드 요청자 ID
     * @param file 업로드할 파일
     * @return 업로드 결과 메시지 및 상대 경로
     */
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // ✅ 파일명 null 검사 및 UUID 추가
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "파일명이 존재하지 않습니다.");
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // ✅ 사용자 디렉토리 생성
            File userDir = new File(BASE_UPLOAD_DIR, String.valueOf(userId));
            if (!userDir.exists() && !userDir.mkdirs()) {
                return ResponseEntity.internalServerError()
                        .body(MessageResponse.of("업로드 경로 생성에 실패했습니다."));
            }

            // ✅ 파일 저장
            File dest = new File(userDir, storedFilename);
            file.transferTo(dest);

            // ✅ 상대 경로 반환
            String relativePath = "/upload/" + userId + "/" + storedFilename;
            return ResponseEntity.ok(MessageResponse.of("파일이 업로드되었습니다.", relativePath));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.of("파일 저장 중 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.of("알 수 없는 오류로 업로드에 실패했습니다."));
        }
    }
}
