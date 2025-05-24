package com.hamcam.back.dto.study.team.websocket;

import lombok.*;

/**
 * [TeamRoomStompMessage]
 * 팀 학습방 STOMP 메시지용 DTO
 * - 발표자 지정, 손들기, 투표, 랭킹 전달 등에 공통 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomStompMessage {

    /**
     * 방 ID
     */
    private Long roomId;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 메시지 타입 또는 액션 (예: RAISE_HAND, SET_PRESENTER, VOTE_START, VOTE_RESPONSE, RANKING_UPDATE)
     */
    private String actionType;

    /**
     * 투표 결과 (true: 성공, false: 실패) - 투표 응답 시 사용
     */
    private Boolean voteResult;

    /**
     * 유연한 데이터 전달용 - 발표자 정보, 랭킹, 메타데이터 등
     */
    private Object data;
}
