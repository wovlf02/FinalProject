package com.hamcam.back.dto.community.post.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시글 요약 응답 DTO (목록 조회용)
 */
@Data
public class PostSimpleResponse {

    private Long postId;
    private String title;
    private String category;
    private String writerNickname;
    private int likeCount;
    private int commentCount;
    private boolean liked;
    private boolean favorite;
    private LocalDateTime createdAt;
}
