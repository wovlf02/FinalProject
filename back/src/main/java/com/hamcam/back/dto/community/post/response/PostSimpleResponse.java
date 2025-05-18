package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [PostSimpleResponse]
 *
 * 게시판 목록, 검색 결과 등에 사용되는 게시글 요약 응답 DTO입니다.
 * 테이블 형태로 렌더링되는 정보를 간결하게 포함합니다.
 */
@Getter
@Builder
public class PostSimpleResponse {

    private Long postId;             // 게시글 ID
    private String category;         // 게시글 카테고리 (예: "INFO", "QUESTION")
    private String title;            // 게시글 제목
    private String writerNickname;  // 작성자 닉네임
    private LocalDateTime createdAt; // 작성일시
    private int viewCount;           // 조회수

    /**
     * Post 엔티티로부터 응답 DTO 생성
     */
    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
                .postId(post.getId())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .writerNickname(post.getWriter().getNickname())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .build();
    }
}
