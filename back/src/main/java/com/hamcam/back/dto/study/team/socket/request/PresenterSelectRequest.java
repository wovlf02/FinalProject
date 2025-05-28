package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenterSelectRequest {
    private Long roomId;
    private Long selectedUserId; // 방장이 선택한 발표자 ID
}
