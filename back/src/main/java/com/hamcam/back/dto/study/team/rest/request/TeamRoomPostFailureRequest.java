package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 실패한 문제를 커뮤니티 게시글로 등록하는 요청 DTO
 */
@Getter
@Setter
public class TeamRoomPostFailureRequest {

    /** 방 ID (어떤 팀 학습방에서 실패했는지 기록용) */
    private Long roomId;

    /** 문제 ID */
    private Long problemId;

    /** 문제 제목 */
    private String problemTitle;

    /** 출처 (예: 2024학년도 6월 모의평가) */
    private String source;

    /** 난이도 (예: 상, 중, 하) */
    private String difficulty;

    /** 사용자 작성 질문 내용 (선택 입력) */
    private String content;

    /** 사용자 작성 해설 내용 (선택 입력) */
    private String explanation;
}
