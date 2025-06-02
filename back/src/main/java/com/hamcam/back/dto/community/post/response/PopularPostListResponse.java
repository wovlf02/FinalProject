package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [PopularPostListResponse]
 *
 * 좋아요 수와 조회수 기준으로 집계된 인기 게시글 목록을 반환하는 응답 DTO입니다.
 * 각 게시글은 카테고리 정보를 포함한 요약 형태(PostSummaryResponse)로 전달됩니다.
 *
 * 예시 응답:
 * {
 *   "posts": [
 *     {
 *       "postId": 1,
 *       "title": "스터디 구합니다",
 *       "writer": "학생A",
 *       "category": "STUDY",
 *       ...
 *     },
 *     ...
 *   ]
 * }
 */
@Getter
@NoArgsConstructor
public class PopularPostListResponse {

    /**
     * 인기 게시글 요약 리스트
     */
    private List<PostSummaryResponse> posts;

    public PopularPostListResponse(List<PostSummaryResponse> posts) {
        this.posts = posts;
    }

    /**
     * Post 엔티티 리스트를 DTO로 변환
     */
    public static PopularPostListResponse from(List<Post> posts) {
        List<PostSummaryResponse> converted = posts.stream()
                .map(PostSummaryResponse::from)
                .collect(Collectors.toList());
        return new PopularPostListResponse(converted);
    }
}
