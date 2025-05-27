// src/main/java/com/hamcam/back/dto/video/request/VideoRoomCreateRequest.java
package com.hamcam.back.dto.video.request;

import com.hamcam.back.entity.video.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 화상 채팅방 생성 요청 DTO
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomCreateRequest {
    private Long hostId;
    private Long teamId;            // ← 여기에 teamId 추가
    private String title;
    private RoomType type;          // QUIZ 또는 FOCUS
    private Integer maxParticipants;
    private String password;        // 선택
    private Integer targetTime;     // FOCUS 전용
}
