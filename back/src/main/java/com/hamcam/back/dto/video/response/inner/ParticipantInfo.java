package com.hamcam.back.dto.video.response.inner;

import com.hamcam.back.entity.video.Participant;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 팀 학습방 참가자 정보 DTO
 */
@Getter
@Builder
public class ParticipantInfo {

    private Long userId;
    private String nickname;
    private String socketId;
    private boolean isPresenter;
    private int focusTime;

    public static ParticipantInfo from(Participant participant) {
        return ParticipantInfo.builder()
                .userId(participant.getUser().getId())
                .nickname(participant.getUser().getNickname())
                .socketId(participant.getSocketId())
                .isPresenter(participant.isPresenter())
                .focusTime(participant.getFocusTime())
                .build();
    }
}
