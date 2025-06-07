package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInfo {

    /** 사용자 ID */
    private Long userId;

    /** 사용자 닉네임 */
    private String nickname;

    /** 누적 집중 시간 (초 단위) */
    private int focusedSeconds;

    /** 결과 확인 여부 (true면 확인 완료 상태) */
    private boolean confirmed;
}
