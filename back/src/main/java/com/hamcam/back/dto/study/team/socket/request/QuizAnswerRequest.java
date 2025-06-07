package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizAnswerRequest {
    private Long roomId;
    private Long problemId;
    private Long userId;
    private String nickname;
    private String answer;
}
