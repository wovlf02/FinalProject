package com.hamcam.back.dto.community.post.request;

import lombok.Data;

import java.util.List;

/**
 * 게시글 수정 요청 DTO
 * <p>
 * 기존 게시글의 제목, 내용, 카테고리를 수정할 때 사용됩니다.
 * </p>
 */
@Data
public class PostUpdateRequest {

    /**
     * 수정할 제목
     */
    private String title;

    /**
     * 수정할 본문
     */
    private String content;

    /**
     * 수정할 카테고리
     */
    private String category;

    private List<Long> deleteFileIds;
}
