package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Post 리스트를 기반으로 PopularPostListResponse 생성
     *
     * @param posts 인기 게시글 엔티티 리스트
     * @return 응답 DTO
     */
    public static PopularPostListResponse from(List<Post> posts) {
        List<PostSimpleResponse> converted = posts.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());
        return new PopularPostListResponse(converted);
    }
}
