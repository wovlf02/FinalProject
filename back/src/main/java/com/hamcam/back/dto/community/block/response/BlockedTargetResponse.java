package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * [BlockedTargetResponse]
 *
 * 차단된 대상(게시글, 댓글, 대댓글 등)의 식별자와 유형 정보를 담는 DTO입니다.
 * 프론트엔드는 이 정보를 기반으로 콘텐츠 필터링 또는 UI 제어를 수행할 수 있습니다.
 */
@Getter
@AllArgsConstructor
@Builder
public class BlockedTargetResponse {

    /**
     * 차단된 대상의 고유 ID
     */
    private final Long targetId;

    /**
     * 차단 대상 유형 (예: "POST", "COMMENT", "REPLY")
     */
    private final String targetType;
}
