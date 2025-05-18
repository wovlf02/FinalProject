package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 상세 조회 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class PostResponse {

    /**
     * 게시글 ID
     */
    private Long postId;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 게시글 카테고리 (nullable)
     */
    private String category;

    /**
     * 작성자 ID
     */
    private Long writerId;

    /**
     * 작성자 닉네임
     */
    private String writerNickname;

    /**
     * 작성자 프로필 이미지 URL
     */
    private String profileImageUrl;

    /**
     * 좋아요 수
     */
    private int likeCount;

    /**
     * 현재 사용자가 좋아요를 눌렀는지 여부
     */
    private boolean liked;

    /**
     * 현재 사용자가 즐겨찾기했는지 여부
     */
    private boolean favorite;

    /**
     * 조회수
     */
    private int viewCount;

    /**
     * 첨부파일 수
     */
    private int attachmentCount;

    /**
     * 댓글 수
     */
    private int commentCount;

    /**
     * 게시글 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 게시글 수정 시각
     */
    private LocalDateTime updatedAt;

    /**
     * 첨부파일 다운로드용 URL 리스트
     */
    private List<String> attachmentUrls;

    /**
     * Post 엔티티를 PostResponse DTO로 변환
     *
     * @param post Post 엔티티
     * @return 변환된 PostResponse 객체
     */
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory().name())
                .writerId(post.getWriter().getId())
                .writerNickname(post.getWriter().getNickname())
                .profileImageUrl(post.getWriter().getProfileImageUrl())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .liked(false) // 실제 값은 서비스에서 동적으로 주입
                .favorite(false) // 실제 값은 서비스에서 동적으로 주입
                .viewCount(post.getViewCount())
                .attachmentCount(post.getAttachments() != null ? post.getAttachments().size() : 0)
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .attachmentUrls(post.getAttachments() != null
                        ? post.getAttachments().stream()
                        .map(att -> "/uploads/community/" + att.getStoredFileName())
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }
}
