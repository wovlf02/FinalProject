package com.hamcam.back.dto.community.like.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [LikeStatusResponse]
 *
 * 로그인한 사용자가 특정 리소스(게시글, 댓글, 대댓글 등)에 좋아요를 눌렀는지 여부를 반환하는 응답 DTO입니다.
 *
 * 예시 응답:
 * {
 *     "liked": true
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeStatusResponse {

    /**
     * 좋아요 여부 (true: 눌렀음, false: 누르지 않음)
     */
    private boolean liked;
}
