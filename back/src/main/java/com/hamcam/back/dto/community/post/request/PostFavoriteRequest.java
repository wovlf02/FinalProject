package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 게시글 즐겨찾기 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFavoriteRequest {

    @NotNull(message = "postId는 필수입니다.")
    private Long postId;

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
