package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시글 요약 응답 DTO (목록 조회용)
 * <p>
 * 게시글의 목록에서 보여줄 필수 정보만 담습니다.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
public class PostSimpleResponse {

    /**
     * 게시글 ID
     */
    private Long postId;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 카테고리
     */
    private String category;

    /**
     * 작성자 닉네임
     */
    private String writerNickname;

    /**
     * 게시글 좋아요 수
     */
    private int likeCount;

    /**
     * 게시글 댓글 수
     */
    private int commentCount;

    /**
     * 현재 로그인 유저가 좋아요를 눌렀는지 여부
     */
    private boolean liked;

    /**
     * 즐겨찾기 등록 여부
     */
    private boolean favorite;

    /**
     * 작성 시각
     */
    private LocalDateTime createdAt;

    /**
     * Post 엔티티로부터 PostSimpleResponse 생성
     *
     * @param post 게시글 엔티티
     * @return 변환된 응답 DTO
     */
    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .writerNickname(post.getWriter().getNickname())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .liked(false) // ❗ 후처리 필요 (현재 로그인 사용자의 좋아요 여부)
                .favorite(false) // ❗ 후처리 필요 (현재 사용자의 즐겨찾기 여부)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
