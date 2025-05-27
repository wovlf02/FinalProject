package com.hamcam.back.dto.study.team.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ Focus 모드 실시간 랭킹 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FocusRankResponse {
    private Long userId;
    private int minutes;
}
