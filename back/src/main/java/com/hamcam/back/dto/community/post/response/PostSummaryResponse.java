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
    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
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
