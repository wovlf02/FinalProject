package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusStudyUpdateRequest {
    private Long roomId;
    private Long userId;      // 세션 기반으로 받아도 되고 프론트에서 넘겨도 됨
    private Integer focusTime;  // 집중한 시간 (단위: 초 or 분)
}
