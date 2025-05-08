package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 요약 응답 DTO
 * <p>
 * 게시글 리스트(목록 조회, 검색 결과, 즐겨찾기 목록 등)에서 사용됩니다.
 * </p>
 */
@Getter
@Builder
public class PostSummaryResponse {

    /**
     * 게시글 ID
     */
    private Long postId;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 작성자 닉네임
     */
    private String writer;

    /**
     * 게시글 카테고리
     */
    private String category;

    /**
     * 좋아요 수
     */
    private int likeCount;

    /**
     * 조회수
     */
    private int viewCount;

    /**
     * 작성일시
     */
    private LocalDateTime createdAt;

    /**
     * Post 엔티티로부터 요약 응답 DTO 생성
     */
    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .writer(post.getWriter().getNickname())
                .category(post.getCategory())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
