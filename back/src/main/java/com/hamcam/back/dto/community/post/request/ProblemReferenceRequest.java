package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 실시간 문제 기반 게시글 자동 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemReferenceRequest {

    @NotNull
    private Long problemId;

    @NotNull
    private Long userId;

    private String problemTitle;
    private String category; // 예: 구현, DFS, 정렬
}
