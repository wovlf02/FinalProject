package com.hamcam.back.dto.community.post.response;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 문제 기반 게시글 자동 완성 응답 DTO
 * - 제목, 본문, 카테고리 자동 추천 결과
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemReferenceResponse {

    /** 자동 생성된 게시글 제목 */
    private String title;

    /** 자동 생성된 게시글 본문 */
    private String content;

    /** 게시글 카테고리 (고정값: QUESTION) */
    private PostCategory category;
}
