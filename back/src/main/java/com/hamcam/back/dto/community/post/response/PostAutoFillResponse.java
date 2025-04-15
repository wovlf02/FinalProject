package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 게시글 자동 완성 응답 DTO
 * <p>
 * 문제 풀이 기반으로 추천된 제목과 내용을 제공
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
public class PostAutoFillResponse {

    /**
     * 추천된 게시글 제목
     */
    private String title;

    /**
     * 추천된 게시글 내용
     */
    private String content;
}
