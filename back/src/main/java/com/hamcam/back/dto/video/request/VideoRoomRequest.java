package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 화상 채팅방 생성 요청 DTO
 * <p>
 * 팀 학습 방(teamId)에 연동된 화상 채팅방을 생성할 때 사용됩니다.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class VideoRoomRequest {

    /**
     * 연동할 팀 스터디방 ID
     */
    private Long teamId;

    /**
     * 생성할 화상 채팅방 제목
     */
    private String title;
}
