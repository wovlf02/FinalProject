package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.*;

import java.util.List;

/**
 * 게시글 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    private String title;
    private String content;
    private PostCategory category;
    private List<Long> deleteFileIds;
}
