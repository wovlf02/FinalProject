package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.*;

/**
 * 게시글 필터링 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFilterRequest {

    private PostCategory category;
    private String sort;
    private int minLikes;
    private String keyword;

    public String getSortOrDefault() {
        return (sort == null || sort.isBlank()) ? "recent" : sort;
    }

    public int getMinLikesOrDefault() {
        return Math.max(minLikes, 0);
    }

    public boolean hasCategory() {
        return category != null;
    }
}
