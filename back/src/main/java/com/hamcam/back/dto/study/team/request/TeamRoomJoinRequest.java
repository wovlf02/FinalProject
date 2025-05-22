package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 스터디방 입장 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomJoinRequest {

    /**
     * 입장하려는 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 입장하려는 방 ID
     */
    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;

    /**
     * 선택적 비밀번호 (비공개 방일 경우 입력)
     */
    private String password;
}
