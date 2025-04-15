package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 차단된 대상 응답 DTO
 * <p>
 * 차단된 게시글, 댓글, 대댓글 등의 ID와 타입을 클라이언트에 전달합니다.
 * </p>
 */
@Data
@AllArgsConstructor
@Builder
public class BlockedTargetResponse {

    /**
     * 차단된 대상의 ID
     */
    private Long targetId;

    /**
     * 대상 타입 (POST, COMMENT, REPLY 등)
     */
    private String targetType;
}
