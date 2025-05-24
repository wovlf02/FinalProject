package com.hamcam.back.dto.community.attachment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PostIdRequest]
 * 게시글 ID를 기반으로 첨부파일 목록을 조회할 때 사용하는 DTO
 */
@Getter
@NoArgsConstructor
public class PostIdRequest {

    @NotNull(message = "postId는 필수입니다.")
    private Long postId;
}
