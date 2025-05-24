package com.hamcam.back.dto.community.post.request;

import lombok.*;

/**
 * 게시글 검색 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchRequest {

    private int page;
    private int size;
    private String keyword;
}
