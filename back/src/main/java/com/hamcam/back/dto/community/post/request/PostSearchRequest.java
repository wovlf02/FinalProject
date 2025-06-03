package com.hamcam.back.dto.community.post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 검색 요청 DTO
 *
 * - 검색 유형: title, content, title_content, author
 * - 검색어: searchTerm
 * - 페이지네이션: page (1부터), size
 */
@Getter
@NoArgsConstructor
public class PostSearchRequest {

    private int page = 1;
    private int size = 10;

    private String searchType;
    private String keyword; // ✅ searchTerm → keyword 로 변경

    public int getPageOrDefault() {
        return page > 0 ? page : 1;
    }

    public int getSizeOrDefault() {
        return size > 0 ? size : 10;
    }

    public String getSearchTypeOrDefault() {
        return (searchType == null || searchType.isBlank()) ? "title_content" : searchType;
    }

    public String getKeywordOrDefault() {
        return (keyword == null || keyword.isBlank()) ? "" : keyword;
    }
}
