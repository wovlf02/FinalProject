package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 풀이자 발표 후 투표 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomVoteRequest {

    /**
     * 투표자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 발표한 메시지(또는 문제) ID
     */
    @NotNull(message = "messageId는 필수입니다.")
    private Long messageId;

    /**
     * 성공 여부 (true=성공, false=실패)
     */
    @NotNull(message = "투표 결과는 필수입니다.")
    private Boolean success;
}
