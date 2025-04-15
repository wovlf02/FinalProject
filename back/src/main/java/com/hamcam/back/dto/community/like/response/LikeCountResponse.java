package com.hamcam.back.dto.community.like.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 좋아요 수 응답 DTO
 * <p>
 * 게시글, 댓글, 대댓글의 총 좋아요 개수를 반환합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class LikeCountResponse {

    /**
     * 좋아요 수
     */
    private int count;
}
