package com.hamcam.back.dto.video.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [VideoRoomCreateRequest]
 * 화상 채팅방 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class VideoRoomCreateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "teamId는 필수입니다.")
    private Long teamId;

    @NotBlank(message = "방 제목은 필수입니다.")
    private String title;
}
