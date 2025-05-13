package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 요약 응답 DTO (게시판 목록, 검색 결과 등에 사용)
 */
@Getter
@Builder
public class PostSimpleResponse {

    private Long postId;             // 게시글 ID
    private String title;            // 게시글 제목
    private String content;          // 게시글 본문 (미리보기용)
    private String writerNickname;  // 작성자 닉네임
    private int likeCount;           // 좋아요 수
    private int viewCount;           // 조회수
    private int commentCount;        // 댓글 수
    private int imageCount;          // 첨부파일 개수
    private LocalDateTime createdAt; // 작성일시

    /**
     * Post 엔티티로부터 응답 DTO 생성
     */
    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerNickname(post.getWriter().getNickname())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentCount())
                .imageCount(post.getAttachments() != null ? post.getAttachments().size() : 0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
