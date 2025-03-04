package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 파일 업로드 응답 DTO
 * 파일 업로드 후 클라이언트에 업로드된 파일의 URL을 반환함
 */
@Getter
@Setter
@AllArgsConstructor
public class FileUploadResponse {

    /**
     * 업로드된 파일 URL 목록
     * 사용자가 업로드한 파일들의 URL 리스트
     */
    private List<String> fileUrls;
}
