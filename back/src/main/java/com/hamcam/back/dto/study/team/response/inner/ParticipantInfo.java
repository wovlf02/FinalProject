package com.hamcam.back.dto.study.team.response.inner;

import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantInfo {

    private Long userId;
    private String nickname;
    private boolean isHost;
    private boolean isReady;
    private int focusedMinutes;

    public static ParticipantInfo from(StudyRoomParticipant p) {
        return ParticipantInfo.builder()
                .userId(p.getUser().getId())
                .nickname(p.getUser().getNickname())
                .isHost(p.isHost())
                .isReady(p.isReady())
                .focusedMinutes(p.getFocusedMinutes())
                .build();
    }
}
