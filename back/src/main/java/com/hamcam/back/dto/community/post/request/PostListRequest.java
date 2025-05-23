package com.hamcam.back.dto.community.post.request;

import lombok.*;

/**
 * 게시글 목록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListRequest {

    private int page;
    private int size;
}
