package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 차단된 게시글 목록 응답 DTO
 * <p>
 * 사용자가 차단한 게시글 정보(게시글 ID와 타입)를 리스트로 제공합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class BlockedPostListResponse {

    /**
     * 차단된 게시글 상세 정보 리스트 (ID + 타입 포함)
     */
    private List<BlockedTargetResponse> blockedPosts;
}
