package com.hamcam.back.dto.study.team.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 발표 투표 전송 메시지
 */
@Getter
@Setter
@NoArgsConstructor
public class VoteMessage {
    private Long roomId;
    private Long userId;
    private boolean success; // true: "풀이 성공", false: "풀이 실패"
}
