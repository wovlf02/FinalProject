// src/main/java/com/hamcam/back/dto/video/request/VideoRoomDetailRequest.java
package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 화상 채팅방 상세 조회 요청 DTO
 */
@Getter @Setter @NoArgsConstructor
public class VideoRoomDetailRequest {
    /** 조회할 방 ID */
    private Long roomId;
}
