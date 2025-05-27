// src/main/java/com/hamcam/back/dto/video/request/VideoRoomUserRequest.java
package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 방 입장/퇴장 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class VideoRoomUserRequest {
    /** 대상 방 ID */
    private Integer roomId;

    /** 접속/퇴장하는 사용자 ID */
    private Long userId;
}
