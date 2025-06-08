package com.hamcam.back.dto.community.like.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요 수 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeCountRequest {
    private Long postId;
}
