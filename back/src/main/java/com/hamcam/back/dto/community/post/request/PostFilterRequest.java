package com.hamcam.back.dto.community.post.request;

import lombok.Data;

/**
 * 게시글 조건별 필터링 요청 DTO
 * <p>
 * 키워드, 카테고리, 정렬 기준, 좋아요 최소 수 등을 포함합니다.
 * </p>
 */
@Data
public class PostFilterRequest {

    /**
     * 카테고리 이름 (optional)
     */
    private String category;

    /**
     * 정렬 기준 (recent, popular 등)
     */
    private String sort;

    /**
     * 최소 좋아요 수 (기본값 0)
     */
    private int minLikes;

    /**
     * 키워드 검색어
     */
    private String keyword;
}
