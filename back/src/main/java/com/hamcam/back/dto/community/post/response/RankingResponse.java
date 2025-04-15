package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 게시글 활동 랭킹 응답 DTO
 * <p>
 * 사용자의 게시글 활동(작성 수, 좋아요 수 등)에 따라 계산된 랭킹 정보
 * </p>
 */
@Data
@AllArgsConstructor
public class RankingResponse {

    private List<UserRanking> rankings;

    @Data
    @AllArgsConstructor
    public static class UserRanking {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private int score; // 활동 점수
    }
}
