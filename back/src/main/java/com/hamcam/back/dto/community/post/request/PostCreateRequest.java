package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PostCreateRequest]
 *
 * 커뮤니티 게시글 작성 요청 DTO입니다.
 * 제목, 내용, 카테고리 정보를 포함하며, 첨부파일은 multipart/form-data로 별도 처리됩니다.
 *
 * 예시 JSON:
 * {
 *   "title": "스터디 모집합니다",
 *   "content": "3학년 기말 대비 스터디 구합니다!",
 *   "category": "STUDY"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 게시글 카테고리 (QUESTION, INFO, STUDY, ANONYMOUS 중 하나)
     */
    private PostCategory category;
}
