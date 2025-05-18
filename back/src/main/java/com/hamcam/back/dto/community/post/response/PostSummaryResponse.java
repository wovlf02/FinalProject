package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [PostSummaryResponse]
 *
 * 게시판 목록, 검색 결과, 즐겨찾기 목록 등에 사용되는 게시글 요약 DTO입니다.
 * 목록 테이블 표시용으로 필요한 필드만 포함합니다.
 */
@Getter
@Builder
public class PostSummaryResponse {

    private Long postId;              // 게시글 ID
    private String category;          // 게시글 카테고리
    private String title;             // 게시글 제목
    private String writerNickname;    // 작성자 닉네임
    private LocalDateTime createdAt;  // 작성일시
    private int viewCount;            // 조회수

    /**
     * Post 엔티티로부터 DTO 생성
     */
    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .category(post.getCategory().name()) // enum → 문자열
                .title(post.getTitle())
                .writerNickname(post.getWriter().getNickname())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .build();
    }
}
