package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 댓글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 댓글의 ID 목록을 제공합니다.
 * 프론트에서는 댓글 목록 렌더링 시 해당 ID를 필터링하여 숨김 처리합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedCommentListResponse {

    /**
     * 차단된 댓글 ID 목록
     */
    private List<Long> blockedCommentIds;
}
