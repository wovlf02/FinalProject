package com.studymate.back.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 채팅방 초대 요청 DTO
 * 단체 채팅방에 사용자를 초대할 때 사용됨
 * 초대할 사용자들의 ID 목록을 포함함
 */
@Getter
@Setter
@NoArgsConstructor
public class InviteRequest {

    /**
     * 초대할 사용자 ID 목록
     * 최소 1명 이상의 사용자를 초대해야 함
     */
    @NotNull(message = "최소 1명 이상의 사용자를 초대해야 합니다.")
    private List<Long> userIds;
}
