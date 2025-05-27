package com.hamcam.back.dto.study.team.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TeamChatMessage {
    private Long roomId;
    private Long userId;
    private String nickname;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
