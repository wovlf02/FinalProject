package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 게시글 목록 조회 응답 DTO
 */
@Data
@AllArgsConstructor
public class PostListResponse {

    /**
     * 게시글 요약 리스트
     */
    private List<PostSimpleResponse> posts;
}
