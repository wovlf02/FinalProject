package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 게시글 작성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "본문은 필수입니다.")
    private String content;

    @NotNull(message = "카테고리는 필수입니다.")
    private PostCategory category;
}
