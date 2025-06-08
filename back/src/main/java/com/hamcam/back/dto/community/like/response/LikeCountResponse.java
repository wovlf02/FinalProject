package com.hamcam.back.dto.community.like.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [LikeCountResponse]
 *
 * 게시글, 댓글, 대댓글의 총 좋아요 개수를 반환하는 응답 DTO입니다.
 * 프론트엔드에서는 이 값을 이용해 좋아요 수를 표시합니다.
 *
 * 예시 응답:
 * {
 *     "count": 42
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCountResponse {

    /**
     * 해당 리소스(게시글, 댓글, 대댓글)의 좋아요 수
     */
    private long count;
}
