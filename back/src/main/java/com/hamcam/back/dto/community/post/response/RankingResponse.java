package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 활동 랭킹 응답 DTO
 * <p>
 * 사용자의 게시글 활동(작성 수, 좋아요 수 등)에 따라 계산된 랭킹 정보를 제공합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class RankingResponse {

    /**
     * 사용자 랭킹 정보 리스트
     */
    private List<UserRanking> rankings;

    /**
     * JPQL 결과(Object[] 배열)를 기반으로 RankingResponse 객체 생성
     *
     * @param rows Object[] = {userId, nickname, profileImageUrl, score}
     * @return RankingResponse
     */
    public static RankingResponse from(List<Object[]> rows) {
        List<UserRanking> rankingList = rows.stream()
                .map(row -> UserRanking.builder()
                        .userId(((Number) row[0]).longValue())
                        .nickname((String) row[1])
                        .profileImageUrl((String) row[2])
                        .score(((Number) row[3]).intValue())
                        .build())
                .collect(Collectors.toList());

        return new RankingResponse(rankingList);
    }

    /**
     * 사용자 개별 랭킹 정보 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class UserRanking {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private int score; // 활동 점수
    }
}
