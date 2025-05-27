// src/main/java/com/hamcam/back/dto/video/request/VideoRoomCreateRequest.java
package com.hamcam.back.dto.video.request;

import com.hamcam.back.entity.video.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 화상 채팅방 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class VideoRoomCreateRequest {
    /** 방장(생성자) 회원 ID */
    private Long hostId;

    /** 연동할 팀 스터디방 ID */
    private Long teamId;

    /** 생성할 화상 채팅방 제목 */
    private String title;

    /** 방 유형: QUIZ 또는 FOCUS */
    private RoomType type;

    /** 최대 참여자 수 */
    private Integer maxParticipants;

    /** 비밀번호 (선택) */
    private String password;

    /** 목표 시간(FOCUS 방 전용, 분 단위) */
    private Integer targetTime;
}
