package com.hamcam.back.dto.community.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 기준 전체 댓글 + 대댓글 계층형 목록 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class CommentListResponse {

    /**
     * 댓글 목록 (대댓글 포함)
     */
    private List<CommentResponse> comments;
}
