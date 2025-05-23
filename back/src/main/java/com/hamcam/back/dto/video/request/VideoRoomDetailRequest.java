package com.hamcam.back.dto.video.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [VideoRoomDetailRequest]
 * 단일 화상 채팅방 상세 요청 DTO
 */
@Getter
@NoArgsConstructor
public class VideoRoomDetailRequest {

    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
