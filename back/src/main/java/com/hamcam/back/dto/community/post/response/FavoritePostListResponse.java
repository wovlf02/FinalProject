package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 즐겨찾기한 게시글 목록 응답 DTO
 */
@Data
@AllArgsConstructor
public class FavoritePostListResponse {

    private List<PostSummaryResponse> posts;
}
