package com.hamcam.back.dto.community.post.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 필터링 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PostFilterRequest {

    private String category;   // 예: "스터디", "정보공유", null은 전체
    private String sort;       // 예: "recent", "likes"
    private int minLikes;      // 기본값 0
    private String keyword;    // 포함 검색

    public String getSortOrDefault() {
        return (sort == null || sort.isBlank()) ? "recent" : sort;
    }

    public int getMinLikesOrDefault() {
        return Math.max(minLikes, 0);
    }

    public boolean hasCategory() {
        return category != null && !category.isBlank();
    }
}
