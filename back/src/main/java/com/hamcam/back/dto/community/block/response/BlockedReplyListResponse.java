package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * [BlockedReplyListResponse]
 *
 * 사용자가 차단한 대댓글(reply) 목록 응답 DTO입니다.
 * 각 항목은 대댓글의 고유 ID와 콘텐츠 타입("REPLY") 정보를 포함합니다.
 */
@Getter
@AllArgsConstructor
public class BlockedReplyListResponse {

    /**
     * 차단된 대댓글 리스트
     */
    private List<BlockedTargetResponse> blockedReplies;
}
