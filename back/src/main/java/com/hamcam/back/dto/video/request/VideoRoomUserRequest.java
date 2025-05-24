package com.hamcam.back.dto.video.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [VideoRoomUserRequest]
 * 방 입장/퇴장/접속자 수 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
public class VideoRoomUserRequest {

    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
