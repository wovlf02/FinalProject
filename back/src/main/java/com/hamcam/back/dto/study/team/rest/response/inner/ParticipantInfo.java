package com.hamcam.back.dto.study.team.rest.response.inner;

import com.hamcam.back.entity.auth.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantInfo {
    private Long userId;
    private String nickname;
    private boolean isHost;

    public static ParticipantInfo from(User user, boolean isHost) {
        return ParticipantInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .isHost(isHost)
                .build();
    }
}
