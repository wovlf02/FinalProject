package com.hamcam.back.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * [FileUtil]
 *
 * 파일 업로드, 다운로드 및 유효성 검사 등을 처리하기 위한 유틸리티 클래스
 *
 * [주요 기능]
 * - 파일 확장자 추출
 * - 파일 MIME 타입 유효성 검사
 * - 파일명에서 원본 이름 추출
 * - MultipartFile → byte[] 변환
 */
public class FileUtil {

    // 허용된 이미지 MIME 타입 목록
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    // 허용된 문서 MIME 타입 목록
    private static final List<String> DOCUMENT_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * [파일 확장자 추출]
     * 파일명에서 확장자만 추출하여 반환
     *
     * @param fileName 원본 파일명 (ex: image.png)
     * @return 확장자 문자열 (ex: png)
     */
    public static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * [이미지 MIME 타입 검사]
     * 허용된 이미지 MIME 타입에 포함되는지 확인
     *
     * @param contentType MultipartFile.getContentType()
     * @return true → 허용된 이미지 파일
     */
    public static boolean isValidImageType(String contentType) {
        return IMAGE_MIME_TYPES.contains(contentType);
    }

    /**
     * [문서 MIME 타입 검사]
     * 허용된 문서 유형인지 검사
     *
     * @param contentType MultipartFile.getContentType()
     * @return true → 허용된 문서 파일
     */
    public static boolean isValidDocumentType(String contentType) {
        return DOCUMENT_MIME_TYPES.contains(contentType);
    }

    /**
     * [파일명에서 원본 이름만 추출]
     * ex) "abc/def/image.png" → "image.png"
     *
     * @param fileName 전체 경로 또는 이름
     * @return 마지막 파일명
     */
    public static String extractFileName(String fileName) {
        if (fileName == null) return null;
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    /**
     * [MultipartFile → byte[] 변환]
     * 파일 저장을 위한 바이트 배열로 변환
     *
     * @param file MultipartFile 객체
     * @return byte 배열
     */
    public static byte[] toByteArray(MultipartFile file) throws IOException {
        return file.getBytes();
    }

    /**
     * [파일 사이즈 검사]
     *
     * @param file MultipartFile
     * @param maxSize 최대 허용 바이트 수 (ex: 10 * 1024 * 1024 = 10MB)
     * @return true → 허용 범위 내
     */
    public static boolean isFileSizeWithinLimit(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }
}
