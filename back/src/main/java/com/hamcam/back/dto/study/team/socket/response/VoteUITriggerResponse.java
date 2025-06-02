package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteUITriggerResponse {

    private String type; // "SHOW_VOTE_UI" 등의 프론트에서 처리할 타입 문자열

}
