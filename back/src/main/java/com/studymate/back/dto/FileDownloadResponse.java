package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 파일 다운로드 응답 DTO
 * 사용자가 파일 다운로드 시 반환되는 데이터
 */
@Getter
@Setter
@AllArgsConstructor
public class FileDownloadResponse {

    /**
     * 파일 이름
     * 사용자가 다운로드할 파일의 이름
     */
    private String fileName;

    /**
     * 파일 데이터
     * 바이너리 형태의 파일 데이터
     */
    private byte[] fileData;

    /**
     * 파일 유형 (MIME Type)
     * 파일의 형식
     */
    private String fileType;
}
