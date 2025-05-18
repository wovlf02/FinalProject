package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PostAutoFillRequest]
 *
 * 문제 기반 게시글 자동 작성 요청 DTO입니다.
 * 문제 ID와 분류 정보를 바탕으로 게시글 제목/내용을 자동 생성합니다.
 *
 * 예시 요청:
 * {
 *   "problemId": 101,
 *   "userId": 5,
 *   "problemTitle": "DFS 탐색 문제",
 *   "category": "QUESTION"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAutoFillRequest {

    /**
     * 문제 ID (추천 게시글 생성 대상)
     */
    private Long problemId;

    /**
     * 요청자 사용자 ID
     */
    private Long userId;

    /**
     * 문제 제목
     */
    private String problemTitle;

    /**
     * 게시글 카테고리 (예: QUESTION, INFO, STUDY, ANONYMOUS)
     */
    private PostCategory category;
}
