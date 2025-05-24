package com.hamcam.back.dto.file.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 요청 DTO (Multipart/form-data 기반)
 */
@Getter
@Setter
@NoArgsConstructor
public class FileUploadRequest {

    private MultipartFile file;
}
