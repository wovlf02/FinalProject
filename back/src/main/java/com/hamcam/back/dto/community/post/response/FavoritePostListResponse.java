package com.hamcam.back.dto.community.post.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [FavoritePostListResponse]
 *
 * 사용자가 즐겨찾기에 추가한 게시글 목록을 반환하는 응답 DTO입니다.
 * 각 게시글은 PostSummaryResponse 형태로 요약되어 전달됩니다.
 *
 * 예시 응답:
 * {
 *   "posts": [
 *     {
 *       "postId": 1,
 *       "title": "공부 방법 공유합니다",
 *       "writerNickname": "학생A",
 *       "likeCount": 12,
 *       "viewCount": 98,
 *       "createdAt": "2024-04-01 13:00"
 *     },
 *     ...
 *   ]
 * }
 */
@Getter
@NoArgsConstructor
public class FavoritePostListResponse {

    /**
     * 즐겨찾기한 게시글들의 요약 정보 리스트
     */
    private List<PostSummaryResponse> posts;

    public FavoritePostListResponse(List<PostSummaryResponse> posts) {
        this.posts = posts;
    }
}
