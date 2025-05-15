package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 목록 조회 응답 DTO
 * 페이징 정보 + 게시글 간략 정보 리스트 반환
 */
@Data
@AllArgsConstructor
public class PostListResponse {

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int currentPage;

    /**
     * 전체 페이지 수
     */
    private int totalPages;

    /**
     * 전체 게시글 수
     */
    private long totalElements;

    /**
     * 한 페이지 당 게시글 수
     */
    private int pageSize;

    /**
     * 게시글 리스트
     */
    private List<PostSimpleResponse> posts;

    /**
     * Page<Post> → PostListResponse 변환
     */
    public static PostListResponse from(Page<Post> page) {
        List<PostSimpleResponse> content = page.getContent().stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());

        return new PostListResponse(
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                content
        );
    }

    /**
     * List<Post> → PostListResponse 변환 (비페이징)
     */
    public static PostListResponse from(List<Post> list) {
        List<PostSimpleResponse> content = list.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());

        return new PostListResponse(
                0,
                1,
                list.size(),
                list.size(),
                content
        );
    }

    private PostListResponse(List<PostSimpleResponse> posts, int currentPage, int totalPages) {
        this.posts = posts;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public static PostListResponse of(Page<PostSimpleResponse> page) {
        return new PostListResponse(page.getContent(), page.getNumber(), page.getTotalPages());
    }
}