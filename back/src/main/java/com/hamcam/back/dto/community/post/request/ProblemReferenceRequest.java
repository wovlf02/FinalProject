package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 실시간 문제 기반 게시글 자동 생성 요청 DTO
 * - 문제풀이방 실패 시 자동 등록용
 */
@Getter
@NoArgsConstructor
public class ProblemReferenceRequest {

    /** 문제 ID (문제 DB 연동 시 사용) */
    @NotNull(message = "문제 ID(problemId)는 필수입니다.")
    private Long problemId;

    /** 문제 제목 (자동완성에 사용됨) */
    private String problemTitle;

    /** 문제 분류 or 전략 태그 (예: 구현, DFS, 정렬 등) */
    private String category;

    /** 출처 정보 (선택) */
    private String source; // 예: 2023 모의고사, 백준 1234번 등
}
