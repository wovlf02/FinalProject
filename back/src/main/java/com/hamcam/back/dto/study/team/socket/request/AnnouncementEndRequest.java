package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementEndRequest {
    private Long roomId;
    private Long presenterId; // 발표자 본인 ID (세션 기반 대체 가능)
}
