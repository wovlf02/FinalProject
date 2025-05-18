package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PostAutoFillResponse]
 *
 * 문제 기반 게시글 자동 완성 응답 DTO입니다.
 * 추천된 제목, 본문, 카테고리 정보를 제공합니다.
 *
 * 예시 응답:
 * {
 *   "title": "문제 풀이 요약: DFS 탐색",
 *   "content": "이 문제는 DFS 전략을 사용하여 해결할 수 있습니다...",
 *   "category": "STUDY"
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAutoFillResponse {

    /**
     * 자동 생성된 게시글 제목
     */
    private String title;

    /**
     * 자동 생성된 게시글 내용
     */
    private String content;

    /**
     * 게시글 카테고리 (QUESTION, INFO, STUDY, ANONYMOUS)
     */
    private PostCategory category;
}
