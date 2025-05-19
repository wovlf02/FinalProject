package com.hamcam.back.dto.community.block.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * [BlockedPostListResponse]
 *
 * 사용자가 차단한 게시글 목록 응답 DTO입니다.
 * 각 게시글은 고유 ID와 차단된 리소스 유형("POST")으로 구성된 객체 리스트입니다.
 */
@Getter
@AllArgsConstructor
public class BlockedPostListResponse {

    /**
     * 차단된 게시글 응답 리스트
     */
    private List<BlockedTargetResponse> blockedPosts;
}
