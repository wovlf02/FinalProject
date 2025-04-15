package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 인기 게시글 목록 응답 DTO
 * <p>
 * 좋아요 수 + 조회수 등 종합 점수를 기준으로 정렬된 게시글 목록
 * </p>
 */
@Data
@AllArgsConstructor
public class PopularPostListResponse {

    private List<PostSimpleResponse> posts;
}
