// src/main/java/com/hamcam/back/dto/video/request/VideoRoomListRequest.java
package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 팀 기준 화상 채팅방 목록 조회 요청 DTO
 */
@Getter @Setter @NoArgsConstructor
public class VideoRoomListRequest {
    /** 조회할 팀 ID */
    private Long teamId;
}
