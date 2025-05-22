package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 문제 기반 게시글 자동완성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAutoFillRequest {

    @NotNull
    private Long problemId;

    @NotNull
    private Long userId;

    private String problemTitle;
    private PostCategory category;
}
