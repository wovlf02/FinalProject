package com.hamcam.back.dto.video.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [VideoRoomListRequest]
 * 특정 팀의 화상 채팅방 목록 요청 DTO
 */
@Getter
@NoArgsConstructor
public class VideoRoomListRequest {

    @NotNull(message = "teamId는 필수입니다.")
    private Long teamId;
}
