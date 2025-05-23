package com.hamcam.back.dto.community.like.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대댓글 좋아요 여부 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeStatusRequest {
    private Long replyId;
    private Long userId;
}
