package com.hamcam.back.dto.community.post.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시글 작성 요청 DTO
 * <p>
 * 제목, 내용, 카테고리, 작성자 정보, 첨부파일 목록을 포함합니다.
 * </p>
 */
@Data
public class PostCreateRequest {

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 작성자 사용자 ID
     */
    private Long writerId;

    /**
     * 게시글 카테고리
     */
    private String category;

    /**
     * 첨부파일 목록
     */
    private List<MultipartFile> attachments;
}
