package com.hamcam.back.dto.study.team.socket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FocusTimeUpdate {
    private Long roomId;
    private Long userId;
    private int deltaMinutes;
}
