package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 문제풀이 시작 요청 DTO (QUIZ 모드 전용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomStartRequest {

    /**
     * 시작 요청자 (방장)
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 스터디방 ID
     */
    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
