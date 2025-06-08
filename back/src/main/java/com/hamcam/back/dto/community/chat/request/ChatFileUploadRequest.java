package com.hamcam.back.dto.community.chat.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 채팅 파일 업로드 요청 DTO
 * - multipart/form-data 기반
 */
@Getter
@Setter
public class ChatFileUploadRequest {

    private Long roomId;
    private MultipartFile file;
}
