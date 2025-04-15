package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 댓글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 댓글(comment)의 상세 차단 정보를 제공합니다.
 * 각 항목은 댓글 ID와 타입("COMMENT")을 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedCommentListResponse {

    /**
     * 차단된 댓글 리스트
     */
    private List<BlockedTargetResponse> blockedComments;
}
