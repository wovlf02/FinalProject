package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 발표에 대한 투표 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class VoteRequest {

    /** 투표 대상 발표자 userId */
    private Long targetUserId;

    /** 찬성 여부 (true: 찬성 / false: 반대) */
    private boolean agree;

    /** 방 ID */
    private Long roomId;
}
