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
 * 페이지네이션 정보와 함께 게시글 요약 정보(PostSimpleResponse)를 포함합니다.
 * 각 게시글은 카테고리, 작성자, 좋아요, 조회수 등의 간단한 정보를 제공합니다.
 *
 * 예시 응답:
 * {
 *   "currentPage": 0,
 *   "totalPages": 3,
 *   "totalElements": 57,
 *   "pageSize": 20,
 *   "posts": [
 *     {
 *       "postId": 1,
 *       "title": "정보 공유합니다",
 *       "category": "INFO",
 *       "writerNickname": "학생A",
 *       ...
 *     },
 *     ...
 *   ]
 * }
 */
@Getter
@Builder
public class PostListResponse {

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private final int currentPage;

    /**
     * 전체 페이지 수
     */
    private final int totalPages;

    /**
     * 전체 게시글 수
     */
    private final long totalElements;

    /**
     * 페이지당 게시글 수
     */
    private final int pageSize;

    /**
     * 게시글 요약 목록 (카테고리 포함)
     */
    private final List<PostSimpleResponse> posts;

    /**
     * Page<Post> → PostListResponse 변환
     */
    public static PostListResponse from(Page<Post> page) {
        List<PostSimpleResponse> content = page.getContent().stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .posts(content)
                .build();
    }

    /**
     * List<Post> → PostListResponse 변환 (비페이지네이션)
     */
    public static PostListResponse from(List<Post> list) {
        List<PostSimpleResponse> content = list.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .currentPage(0)
                .totalPages(1)
                .totalElements(list.size())
                .pageSize(list.size())
                .posts(content)
                .build();
    }
}
