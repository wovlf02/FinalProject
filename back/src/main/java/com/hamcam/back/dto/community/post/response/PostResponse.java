package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Attachment;
import com.hamcam.back.entity.community.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 상세 조회 응답 DTO
 */
@Data
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 게시글에 첨부된 파일 URL 목록
     */
    private List<String> attachmentUrls;

    /**
     * Post 엔티티 → PostResponse DTO로 변환
     */
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .writerId(post.getWriter().getId())
                .writerNickname(post.getWriter().getNickname())
                .profileImageUrl(post.getWriter().getProfileImageUrl())
                .likeCount(post.getLikes().size())
                .liked(false) // 로그인 사용자 기준으로 이후 처리 필요
                .favorite(false) // 로그인 사용자 기준으로 이후 처리 필요
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .attachmentUrls(post.getAttachments() != null
                        ? post.getAttachments().stream()
                        .map(Attachment::getFileUrl) // 또는 getOriginalName(), getPath() 등 원하는 필드
                        .collect(Collectors.toList())
                        : List.of()
                )
                .build();
    }
}
