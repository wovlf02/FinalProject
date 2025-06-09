package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [PostListResponse]
 *
 * 게시글 목록 조회 응답 DTO입니다.
 * 페이지네이션 정보, 필터링 조건(category, keyword), 게시글 요약 리스트를 포함합니다.
 */
@Getter
@Builder
public class PostListResponse {

    private final int currentPage;        // 현재 페이지 번호 (0부터 시작)
    private final int totalPages;         // 전체 페이지 수
    private final long totalElements;     // 전체 게시글 수
    private final int pageSize;           // 페이지당 게시글 수
    private final String category;        // 필터링된 카테고리 (null이면 전체)
    private final String keyword;         // 검색 키워드 (null이면 전체)
    private final List<PostSummaryResponse> posts; // 게시글 요약 목록

    /**
     * Page<Post> → PostListResponse 변환 + 필터링 조건 포함
     */
    public static PostListResponse from(Page<Post> page, String category, String keyword) {
        return PostListResponse.builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .category(category)
                .keyword(keyword)
                .posts(page.getContent().stream()
                        .map(PostSummaryResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * List<Post> → PostListResponse 변환 (비페이지네이션, 전체목록 용)
     */
    public static PostListResponse from(List<Post> list) {
        return PostListResponse.builder()
                .currentPage(0)
                .totalPages(1)
                .totalElements(list.size())
                .pageSize(list.size())
                .category(null)
                .keyword(null)
                .posts(list.stream()
                        .map(PostSummaryResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
