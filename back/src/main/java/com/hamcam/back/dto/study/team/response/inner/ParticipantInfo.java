package com.hamcam.back.dto.study.team.response.inner;

import com.hamcam.back.entity.auth.User;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 팀방 내 참가자 정보
 */
@Getter
@Builder
public class ParticipantInfo {
    private Long userId;
    private String nickname;

    public static ParticipantInfo from(User user) {
        return ParticipantInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
