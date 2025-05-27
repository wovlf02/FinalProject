package com.hamcam.back.dto.video.request;

import com.hamcam.back.entity.video.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 팀 학습방 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequest {

    /** 방 제목 */
    private String title;

    /** 방 비밀번호 (선택) */
    private String password;

    /** 최대 참가자 수 (예: 3~10명 제한 가능) */
    private int maxParticipants;

    /** 방 모드: QUIZ(문제풀이), FOCUS(집중경쟁) */
    private RoomType roomType;
}
