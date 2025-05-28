package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRoomJoinRequest {
    private String inviteCode;
    private String password;  // 선택적으로 입력
}
