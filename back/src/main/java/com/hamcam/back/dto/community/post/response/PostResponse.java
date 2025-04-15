package com.hamcam.back.dto.community.post.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 응답 DTO
 */
@Data
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
}
