package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 대댓글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 대댓글(reply)의 상세 차단 정보를 제공합니다.
 * 각 항목은 차단된 콘텐츠 ID와 타입("REPLY")을 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedReplyListResponse {

    /**
     * 차단된 대댓글 정보 리스트
     */
    private List<BlockedTargetResponse> blockedReplies;
}
