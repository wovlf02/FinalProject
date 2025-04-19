package com.hamcam.back.dto.community.post.request;

import lombok.Data;

/**
 * 실시간 문제풀이방 → 게시글 자동 완성 요청 DTO
 * <p>
 * 문제 풀이 기반으로 게시글을 생성할 때 사용됩니다.
 * 예: 문제 ID에 대한 풀이 요약을 본문으로 생성
 * </p>
 */
@Data
public class ProblemReferenceRequest {

    /**
     * 참조할 문제 ID
     */
    private Long problemId;

    /**
     * 요청자 사용자 ID
     */
    private Long userId;

    /**
     * 문제 제목
     * 게시글 자동 완성 시 제목 추천에 사용됨
     */
    private String problemTitle;

    /**
     * 문제 분류 또는 카테고리
     * (예: 구현, DFS, DP 등 → 본문 내용 생성에 사용됨)
     */
    private String category;
}
