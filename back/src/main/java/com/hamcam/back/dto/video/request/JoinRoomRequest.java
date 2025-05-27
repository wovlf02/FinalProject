package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 팀 학습방 입장 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class JoinRoomRequest {

    /** 방 ID (선택, inviteCode와 택1) */
    private Long roomId;

    /** 초대 코드 (선택, roomId와 택1) */
    private String inviteCode;

    /** 비밀번호 (설정된 방일 경우 필요) */
    private String password;

    /** WebRTC 연결을 위한 socketId */
    private String socketId;
}
