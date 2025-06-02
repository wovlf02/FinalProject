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

    private Long postId;
    private String title;
    private String content;
    private String category;
    private Long writerId;
    private String writerNickname;
    private String profileImageUrl;
    private int likeCount;
    private boolean liked;
    private boolean favorite;
    private int viewCount;
    private int attachmentCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> attachmentUrls;

    /**
     * Post 엔티티를 PostResponse DTO로 변환
     * liked / favorite 은 서비스에서 추가로 세팅 필요
     */
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory() != null ? post.getCategory().name() : null)
                .writerId(post.getWriter().getId())
                .writerNickname(post.getWriter().getNickname())
                .profileImageUrl(post.getWriter().getProfileImageUrl())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .liked(false)
                .favorite(false)
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
