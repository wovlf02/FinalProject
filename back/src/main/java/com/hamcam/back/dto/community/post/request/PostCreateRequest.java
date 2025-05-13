package com.hamcam.back.dto.community.post.request;

import lombok.*;

/**
 * 게시글 작성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    private String content;
    private Long writerId;
}
