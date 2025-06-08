package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 투표 제출 요청
 */
@Getter
@Setter
public class VoteSubmitRequest {
    private Long roomId;
    private VoteType vote; // "SUCCESS" 또는 "FAIL"
}
