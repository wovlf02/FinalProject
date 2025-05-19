package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PostFilterRequest]
 *
 * 카테고리, 정렬 기준, 좋아요 수, 키워드 등을 포함한 게시글 필터링 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFilterRequest {

    /**
     * 게시글 카테고리 (null 가능, "QUESTION", "INFO", "STUDY", "ANONYMOUS")
     */
    private PostCategory category;

    /**
     * 정렬 기준 (recent | popular)
     */
    private String sort;

    /**
     * 최소 좋아요 수 (기본값: 0)
     */
    private int minLikes;

    /**
     * 키워드 검색어 (제목/본문 대상, optional)
     */
    private String keyword;

    /**
     * 정렬 기준 기본값 반환
     */
    public String getSortOrDefault() {
        return (sort == null || sort.isBlank()) ? "recent" : sort;
    }

    /**
     * 최소 좋아요 수 기본값 보정
     */
    public int getMinLikesOrDefault() {
        return Math.max(minLikes, 0);
    }

    /**
     * 카테고리 필터 존재 여부
     */
    public boolean hasCategory() {
        return category != null;
    }
}
