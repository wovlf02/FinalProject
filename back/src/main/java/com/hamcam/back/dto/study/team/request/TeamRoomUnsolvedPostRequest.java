package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomUnsolvedPostRequest {

    private Long userId;
    private Long roomId;

    /** 커뮤니티 업로드용 자동 생성된 제목 */
    private String autoFilledTitle;

    /** 문제 설명 or 질문 내용 */
    private String questionContent;
}
