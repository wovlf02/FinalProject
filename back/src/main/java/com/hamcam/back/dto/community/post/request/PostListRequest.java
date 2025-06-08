package com.hamcam.back.dto.community.post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 목록 요청 DTO
 * - 전체 또는 카테고리별 목록 조회 (페이징 + 검색 포함)
 */
@Getter
@NoArgsConstructor
public class PostListRequest {

    private int page = 1;
    private int size = 10;

    private String category;      // 게시글 카테고리
    private String searchType;    // title, content, title_content, author
    private String keyword;       // 검색어

    public int getPageOrDefault() {
        return page > 0 ? page : 1;
    }

    public int getSizeOrDefault() {
        return size > 0 ? size : 10;
    }

    public String getCategoryOrNull() {
        return (category == null || category.trim().isEmpty()) ? null : category.trim();
    }

    public String getSearchTypeOrNull() {
        return (searchType == null || searchType.trim().isEmpty()) ? null : searchType.trim();
    }

    public String getKeywordOrNull() {
        return (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
    }
}
