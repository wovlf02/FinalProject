package com.hamcam.back.dto.community.post.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ProblemReferenceRequest]
 *
 * 실시간 문제풀이방에서 게시글 자동 생성을 요청할 때 사용하는 DTO입니다.
 * 문제 제목과 알고리즘 분류 등을 기반으로 GPT 또는 템플릿을 활용한 제목/본문 자동 완성을 지원합니다.
 *
 * 예시 요청:
 * {
 *   "problemId": 101,
 *   "userId": 7,
 *   "problemTitle": "DFS 탐색",
 *   "category": "DFS"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemReferenceRequest {

    /**
     * 참조할 문제 ID
     */
    private Long problemId;

    /**
     * 요청한 사용자 ID
     */
    private Long userId;

    /**
     * 문제 제목 (자동 완성 게시글 제목에 사용됨)
     */
    private String problemTitle;

    /**
     * 문제 분류 또는 알고리즘 유형 (e.g. 구현, DFS, 정렬)
     */
    private String category;
}
