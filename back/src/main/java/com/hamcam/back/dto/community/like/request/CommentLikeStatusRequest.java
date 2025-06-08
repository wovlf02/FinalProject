package com.hamcam.back.dto.community.like.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 좋아요 여부 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeStatusRequest {
    private Long commentId;
}