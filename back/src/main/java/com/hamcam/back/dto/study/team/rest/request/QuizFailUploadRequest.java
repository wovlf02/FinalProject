package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizFailUploadRequest {
    private Long roomId;
    private Long problemId;
    private String questionTitle;
    private String content;  // 질문 내용 (자동 생성 or 유저 작성)
}
