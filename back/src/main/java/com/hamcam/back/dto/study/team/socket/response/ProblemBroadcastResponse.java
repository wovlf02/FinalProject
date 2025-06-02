package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProblemBroadcastResponse {
    private String title;
    private String content;
    private String[] choices;
    private String explanation;
}
