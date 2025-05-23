package com.hamcam.back.dto.community.like.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 좋아요 토글 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeToggleRequest {
    private Long commentId;
    private Long userId;
}
