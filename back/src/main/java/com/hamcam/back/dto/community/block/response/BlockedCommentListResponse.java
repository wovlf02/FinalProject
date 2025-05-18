package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * [BlockedCommentListResponse]
 *
 * 사용자가 차단한 댓글 목록 응답 DTO입니다.
 * 각 댓글은 고유 ID와 차단된 리소스 유형("COMMENT")으로 식별됩니다.
 */
@Getter
@AllArgsConstructor
public class BlockedCommentListResponse {

    /**
     * 차단된 댓글 응답 리스트
     */
    private List<BlockedTargetResponse> blockedComments;
}
