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
    private String content;           // ✅ 게시글 내용 (추가)
    private String writerNickname;    // 작성자 닉네임
    private LocalDateTime createdAt;  // 작성일시
    private int viewCount;            // 조회수
    private int likeCount;            // 좋아요 수 (선택적으로 프론트에서 사용 가능)
    private int commentCount;         // 댓글 수 (선택적으로 프론트에서 사용 가능)

    /**
     * Post 엔티티로부터 DTO 생성
     */
    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .category(post.getCategory() != null ? post.getCategory().name() : null)
                .title(post.getTitle())
                .content(post.getContent()) // ✅ 추가
                .writerNickname(post.getWriter().getNickname())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())        // 선택 필드
                .commentCount(post.getCommentCount())  // 선택 필드
                .build();
    }
}
