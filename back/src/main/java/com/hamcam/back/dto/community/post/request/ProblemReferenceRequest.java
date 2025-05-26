package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 실시간 문제 기반 게시글 자동 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class ProblemReferenceRequest {

    @NotNull(message = "문제 ID(problemId)는 필수입니다.")
    private Long problemId;

    private String problemTitle;  // 선택값 (있으면 활용)
    private String category;      // 예: 구현, DFS, 정렬 등 문제 분류 태그
}
