package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * JPQL 결과(Object[]) 리스트를 기반으로 응답 DTO 생성
     *
     * @param rows Object[] = {userId, nickname, profileImageUrl, score}
     * @return RankingResponse
     */
    public static RankingResponse from(List<Object[]> rows) {
        List<UserRanking> rankingList = rows.stream()
                .map(row -> new UserRanking(
                        ((Number) row[0]).longValue(),        // userId
                        (String) row[1],                      // nickname
                        (String) row[2],                      // profileImageUrl
                        ((Number) row[3]).intValue()          // score
                ))
                .collect(Collectors.toList());
        return new RankingResponse(rankingList);
    }

    @Data
    @AllArgsConstructor
    public static class UserRanking {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private int score; // 활동 점수
    }
}
