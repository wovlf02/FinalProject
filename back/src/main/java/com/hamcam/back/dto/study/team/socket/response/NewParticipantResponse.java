package com.hamcam.back.dto.study.team.socket.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewParticipantResponse {
    private Long userId;
    private String nickname;
    private boolean isHost;
}
