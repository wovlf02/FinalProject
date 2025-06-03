package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 삭제 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PostDeleteRequest {

    @NotNull(message = "게시글 ID(postId)는 필수입니다.")
    private Long postId;
}
