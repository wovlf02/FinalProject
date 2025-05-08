package com.hamcam.back.dto.community.post.request;

import lombok.Data;

/**
 * 게시글 작성 요청 DTO
 */
@Data
public class PostCreateRequest {

    private String title;
    private String content;
    private Long writerId;
    private String category;
}
