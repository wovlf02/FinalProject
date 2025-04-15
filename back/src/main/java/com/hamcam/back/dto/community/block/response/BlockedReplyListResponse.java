package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 대댓글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 대댓글(reply)의 ID 목록을 제공합니다.
 * 프론트에서 해당 ID를 렌더링하지 않도록 처리할 수 있습니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedReplyListResponse {

    /**
     * 차단된 대댓글 ID 목록
     */
    private List<Long> blockedReplyIds;
}
