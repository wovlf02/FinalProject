package com.hamcam.back.dto.community.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 기준 전체 댓글 + 대댓글 계층형 목록 응답 DTO
 *
 * <p>
 * 각 댓글(CommentResponse)은 자신의 대댓글 리스트를 포함하며,
 * 프론트에서는 이를 계층적으로 렌더링할 수 있습니다.
 * </p>
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
