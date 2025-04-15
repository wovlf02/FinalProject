package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 게시글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 게시글들의 ID 목록을 반환합니다.
 * 프론트에서는 이 ID를 이용해 피드에서 숨김 처리할 수 있습니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedPostListResponse {

    /**
     * 차단된 게시글 ID 목록
     */
    private List<Long> blockedPostIds;
}
