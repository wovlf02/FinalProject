package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomUnsolvedPostRequest {

    private Long userId;
    private Long roomId;

    /** 게시글 제목 (예: "[질문] 문제 3번 관련 질문") */
    private String title;

    /** 게시글 내용 */
    private String content;
}
