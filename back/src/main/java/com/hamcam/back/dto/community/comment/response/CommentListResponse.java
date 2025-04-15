package com.hamcam.back.dto.community.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 게시글 기준 전체 댓글 + 대댓글 계층형 목록 응답 DTO
 * <p>
 * 프론트에서 계층 구조로 표현할 수 있도록, 각 댓글에 대댓글 리스트가 포함됩니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class CommentListResponse {

    /**
     * 댓글 목록 (대댓글 포함)
     */
    private List<CommentResponse> comments;
}
